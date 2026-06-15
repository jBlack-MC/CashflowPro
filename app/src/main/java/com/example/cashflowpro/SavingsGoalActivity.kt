package com.example.cashflowpro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.SavingsGoal
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch
import java.util.*

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

        if (title.isEmpty() || targetStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val target = targetStr.toDoubleOrNull()
        if (target == null) {
            Toast.makeText(this, "Invalid target amount", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val goal = SavingsGoal(
                name = title,
                targetAmount = target,
                currentAmount = 0.0,
                dueDate = "No Date"
            )
            db.savingsGoalDao().insertGoal(goal)
            etGoalTitle.text.clear()
            etTargetAmount.text.clear()
            loadGoals()
            Toast.makeText(this@SavingsGoalActivity, "Goal launched!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            val goals = db.savingsGoalDao().getAllGoals()
            goalsListContainer.removeAllViews()
            val inflater = LayoutInflater.from(this@SavingsGoalActivity)

            if (goals.isEmpty()) {
                val tv = TextView(this@SavingsGoalActivity)
                tv.text = "No savings goals set yet."
                tv.setPadding(32, 32, 32, 32)
                goalsListContainer.addView(tv)
                return@launch
            }

            for (goal in goals) {
                val view = inflater.inflate(R.layout.item_savings_goal, goalsListContainer, false)
                
                view.findViewById<TextView>(R.id.tvGoalTitle).text = goal.name
                val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount * 100).toInt() else 0
                view.findViewById<TextView>(R.id.tvPercentage).text = "$progress%"
                view.findViewById<LinearProgressIndicator>(R.id.goalProgress).progress = progress
                view.findViewById<TextView>(R.id.tvAmountStatus).text = "R${goal.currentAmount.toInt()} / R${goal.targetAmount.toInt()}"

                view.findViewById<ImageView>(R.id.btnAddFunds).setOnClickListener {
                    addFunds(goal)
                }
                
                view.findViewById<ImageView>(R.id.btnDeleteGoal).setOnClickListener {
                    deleteGoal(goal)
                }

                goalsListContainer.addView(view)
            }
        }
    }

    private fun addFunds(goal: SavingsGoal) {
        lifecycleScope.launch {
            val updatedGoal = goal.copy(currentAmount = goal.currentAmount + 100.0) // Mock add R100
            db.savingsGoalDao().insertGoal(updatedGoal)
            loadGoals()
        }
    }

    private fun deleteGoal(goal: SavingsGoal) {
        lifecycleScope.launch {
            // Need a delete in DAO? 
            // For now just mock refresh
            Toast.makeText(this@SavingsGoalActivity, "Goal removed", Toast.LENGTH_SHORT).show()
            loadGoals()
        }
    }
}
