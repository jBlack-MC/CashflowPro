package com.example.cashflowpro

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.SavingsGoal
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch
import java.util.Locale

class SavingsGoalActivity : AppCompatActivity() {

    private lateinit var etGoalTitle: EditText
    private lateinit var etTargetAmount: EditText
    private lateinit var btnAddGoal: Button
    private lateinit var goalsListContainer: LinearLayout
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_goal)

        setupToolbar()
        initViews()
        loadGoals()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        etGoalTitle = findViewById(R.id.etGoalTitle)
        etTargetAmount = findViewById(R.id.etTargetAmount)
        btnAddGoal = findViewById(R.id.btnAddGoal)
        goalsListContainer = findViewById(R.id.goalsListContainer)

        btnAddGoal.setOnClickListener {
            saveGoal()
        }
    }

    private fun saveGoal() {
        val title = etGoalTitle.text.toString().trim()
        val targetStr = etTargetAmount.text.toString().trim()
        val target = targetStr.toDoubleOrNull() ?: 0.0

        if (title.isEmpty() || target <= 0) {
            Toast.makeText(this, "Please enter valid title and amount", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            db.savingsGoalDao().insertGoal(SavingsGoal(title = title, targetAmount = target, currentAmount = 0.0))
            etGoalTitle.text.clear()
            etTargetAmount.text.clear()
            loadGoals()
        }
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            val goals = db.savingsGoalDao().getAllGoals()
            displayGoals(goals)
        }
    }

    private fun displayGoals(goals: List<SavingsGoal>) {
        goalsListContainer.removeAllViews()
        for (goal in goals) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_savings_goal, goalsListContainer, false)
            
            val tvTitle = itemView.findViewById<TextView>(R.id.tvGoalTitle)
            val tvPercent = itemView.findViewById<TextView>(R.id.tvPercentage)
            val tvStatus = itemView.findViewById<TextView>(R.id.tvAmountStatus)
            val progress = itemView.findViewById<LinearProgressIndicator>(R.id.goalProgress)
            val btnAdd = itemView.findViewById<ImageView>(R.id.btnAddFunds)
            val btnDelete = itemView.findViewById<ImageView>(R.id.btnDeleteGoal)

            val percentage = (goal.currentAmount / goal.targetAmount * 100).toInt().coerceIn(0, 100)
            
            tvTitle.text = goal.title
            tvPercent.text = String.format(Locale.getDefault(), "%d%%", percentage)
            tvStatus.text = String.format(Locale.getDefault(), "R%.0f / R%.0f", goal.currentAmount, goal.targetAmount)
            progress.progress = percentage

            btnAdd.setOnClickListener { showAddFundsDialog(goal) }
            btnDelete.setOnClickListener {
                lifecycleScope.launch {
                    db.savingsGoalDao().deleteGoal(goal)
                    loadGoals()
                }
            }

            goalsListContainer.addView(itemView)
        }
    }

    private fun showAddFundsDialog(goal: SavingsGoal) {
        val editText = EditText(this)
        editText.hint = "Amount to add"
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        
        AlertDialog.Builder(this)
            .setTitle("Add Funds to ${goal.title}")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val amount = editText.text.toString().toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    lifecycleScope.launch {
                        db.savingsGoalDao().updateGoal(goal.copy(currentAmount = goal.currentAmount + amount))
                        loadGoals()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
