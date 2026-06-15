package com.example.cashflowpro

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Budget
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class GoalActivity : AppCompatActivity() {

    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnSaveGoals: Button
    private lateinit var statusCard: MaterialCardView
    private lateinit var tvStatus: TextView

    private lateinit var viewGoalTrack: View
    private lateinit var viewGoalProgress: View
    private lateinit var viewMinMarker: View
    private lateinit var tvMinLabel: TextView
    private lateinit var tvMaxLabel: TextView

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        setupToolbar()
        initViews()
        loadGoals()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        btnSaveGoals = findViewById(R.id.btnSaveGoals)
        statusCard = findViewById(R.id.statusCard)
        tvStatus = findViewById(R.id.tvStatus)

        viewGoalTrack = findViewById(R.id.viewGoalTrack)
        viewGoalProgress = findViewById(R.id.viewGoalProgress)
        viewMinMarker = findViewById(R.id.viewMinMarker)
        tvMinLabel = findViewById(R.id.tvMinLabel)
        tvMaxLabel = findViewById(R.id.tvMaxLabel)

        btnSaveGoals.setOnClickListener {
            saveGoals()
        }
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            val budget = db.budgetDao().getBudget()
            budget?.let {
                etMinGoal.setText(it.min.toString())
                etMaxGoal.setText(it.max.toString())
                checkStatus(it)
            }
        }
    }

    private fun saveGoals() {
        val minStr = etMinGoal.text.toString().trim()
        val maxStr = etMaxGoal.text.toString().trim()

        if (minStr.isEmpty() || maxStr.isEmpty()) {
            Toast.makeText(this, "Please fill both goals", Toast.LENGTH_SHORT).show()
            return
        }

        val min = minStr.toDoubleOrNull()
        val max = maxStr.toDoubleOrNull()

        if (min == null || max == null) {
            Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val budget = Budget(id = 1, min = min, max = max) // Use fixed ID to always overwrite the single budget record
            db.budgetDao().insertBudget(budget)
            Log.d("Goal", "Goal Saved: Min=$min, Max=$max")
            Toast.makeText(this@GoalActivity, "Goals saved", Toast.LENGTH_SHORT).show()
            checkStatus(budget)
        }
    }

    private fun checkStatus(budget: Budget) {
        lifecycleScope.launch {
            val totalExpenses = db.expenseDao().getTotalExpenses() ?: 0.0
            statusCard.visibility = View.VISIBLE

            tvMinLabel.text = String.format("R%.0f", budget.min)
            tvMaxLabel.text = String.format("R%.0f", budget.max)
            
            when {
                totalExpenses < budget.min -> {
                    tvStatus.text = String.format("Below Minimum: R%.0f", totalExpenses)
                    tvStatus.setTextColor(getColor(R.color.warning))
                    viewGoalProgress.setBackgroundColor(getColor(R.color.warning))
                }
                totalExpenses > budget.max -> {
                    tvStatus.text = String.format("Over Budget: R%.0f", totalExpenses)
                    tvStatus.setTextColor(getColor(R.color.error))
                    viewGoalProgress.setBackgroundColor(getColor(R.color.error))
                }
                else -> {
                    tvStatus.text = String.format("Within Goal: R%.0f", totalExpenses)
                    tvStatus.setTextColor(getColor(R.color.success_dark))
                    viewGoalProgress.setBackgroundColor(getColor(R.color.success))
                }
            }

            viewGoalTrack.post {
                val trackWidth = viewGoalTrack.width
                if (trackWidth > 0) {
                    val progressRatio = (totalExpenses / budget.max).coerceIn(0.0, 1.0).toFloat()
                    val progressParams = viewGoalProgress.layoutParams
                    progressParams.width = (trackWidth * progressRatio).toInt()
                    viewGoalProgress.layoutParams = progressParams

                    val minRatio = (budget.min / budget.max).coerceIn(0.0, 1.0).toFloat()
                    val markerParams = viewMinMarker.layoutParams as android.widget.RelativeLayout.LayoutParams
                    markerParams.marginStart = (trackWidth * minRatio).toInt()
                    viewMinMarker.layoutParams = markerParams
                }
            }
        }
    }
}
