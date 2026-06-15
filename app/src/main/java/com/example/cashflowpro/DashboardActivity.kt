package com.example.cashflowpro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTotalBudget: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvPercentUsed: TextView
    private lateinit var tvLeftLabel: TextView
    
    private lateinit var tvGroceriesSpent: TextView
    private lateinit var tvTransportSpent: TextView
    private lateinit var tvEntertainmentSpent: TextView
    
    private lateinit var budgetProgress: LinearProgressIndicator
    private lateinit var btnAddExpenseFab: FloatingActionButton

    // Goal Range Views
    private lateinit var tvGoalStatus: TextView
    private lateinit var viewGoalProgress: View
    private lateinit var viewGoalTrack: View
    private lateinit var viewMinMarker: View
    private lateinit var tvMinLabel: TextView
    private lateinit var tvMaxLabel: TextView
    private lateinit var tvGoalDescription: TextView

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        
        Log.d("DashboardActivity", "Dashboard activity created")

        initViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        Log.i("DashboardActivity", "Refreshing dashboard data")
        updateDashboardData()
    }

    private fun initViews() {
        tvTotalBudget = findViewById(R.id.tvTotalBudget)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvRemaining = findViewById(R.id.tvRemaining)
        tvPercentUsed = findViewById(R.id.tvPercentUsed)
        tvLeftLabel = findViewById(R.id.tvLeftLabel)
        
        tvGroceriesSpent = findViewById(R.id.tvGroceriesSpent)
        tvTransportSpent = findViewById(R.id.tvTransportSpent)
        tvEntertainmentSpent = findViewById(R.id.tvEntertainmentSpent)
        
        budgetProgress = findViewById(R.id.budgetProgress)
        btnAddExpenseFab = findViewById(R.id.btnAddExpenseFab)

        tvGoalStatus = findViewById(R.id.tvGoalStatus)
        viewGoalProgress = findViewById(R.id.viewGoalProgress)
        viewGoalTrack = findViewById(R.id.viewGoalTrack)
        viewMinMarker = findViewById(R.id.viewMinMarker)
        tvMinLabel = findViewById(R.id.tvMinLabel)
        tvMaxLabel = findViewById(R.id.tvMaxLabel)
        tvGoalDescription = findViewById(R.id.tvGoalDescription)
    }

    private fun setupListeners() {
        btnAddExpenseFab.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        findViewById<View>(R.id.actionAdd).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        findViewById<View>(R.id.actionHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<View>(R.id.actionAnalytics).setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
        }

        findViewById<View>(R.id.actionCategories).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        findViewById<View>(R.id.actionGoals).setOnClickListener {
            startActivity(Intent(this, GoalActivity::class.java))
        }

        findViewById<View>(R.id.actionRewards).setOnClickListener {
            startActivity(Intent(this, BadgeActivity::class.java))
        }

        findViewById<View>(R.id.actionSavings).setOnClickListener {
            startActivity(Intent(this, SavingsGoalActivity::class.java))
        }

        findViewById<View>(R.id.actionReports).setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }
    }

    private fun updateDashboardData() {
        lifecycleScope.launch {
            try {
                val budget = db.budgetDao().getBudget()
                val totalMonthlyBudget = budget?.max ?: 5000.0

                val totalSpent = db.expenseDao().getTotalExpenses() ?: 0.0
                val groceriesSpent = db.expenseDao().getTotalByCategory("Groceries") ?: 0.0
                val transportSpent = db.expenseDao().getTotalByCategory("Transport") ?: 0.0
                val entertainmentSpent = db.expenseDao().getTotalByCategory("Entertainment") ?: 0.0

                val remaining = totalMonthlyBudget - totalSpent
                val percentUsed = (totalSpent / totalMonthlyBudget * 100).coerceIn(0.0, 100.0)

                // Update Overview
                tvTotalBudget.text = String.format(Locale.getDefault(), "R%.0f", totalMonthlyBudget)
                tvTotalSpent.text = String.format(Locale.getDefault(), "R%.0f", totalSpent)
                tvRemaining.text = String.format(Locale.getDefault(), "R%.0f", remaining)
                tvPercentUsed.text = String.format(Locale.getDefault(), "%.0f%%", percentUsed)
                tvLeftLabel.text = String.format(Locale.getDefault(), "R%.0f left until you reach your limit", remaining)

                // Update Categories
                tvGroceriesSpent.text = String.format(Locale.getDefault(), "R%.0f", groceriesSpent)
                tvTransportSpent.text = String.format(Locale.getDefault(), "R%.0f", transportSpent)
                tvEntertainmentSpent.text = String.format(Locale.getDefault(), "R%.0f", entertainmentSpent)

                // Update Progress Indicator
                budgetProgress.progress = percentUsed.toInt()
                
                updateGoalRangeVisuals(totalSpent, budget?.min ?: 2000.0, totalMonthlyBudget)

                Log.d("DashboardActivity", "Dashboard updated. Total spent: R$totalSpent")

            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error updating dashboard", e)
            }
        }
    }

    private fun updateGoalRangeVisuals(spent: Double, min: Double, max: Double) {
        tvMinLabel.text = String.format(Locale.getDefault(), "R%.0f", min)
        tvMaxLabel.text = String.format(Locale.getDefault(), "R%.0f", max)
        tvGoalDescription.text = String.format(Locale.getDefault(), "Target range: R%.0f - R%.0f", min, max)

        val statusText: String
        val statusColor: Int
        val progressColor: Int

        when {
            spent < min -> {
                statusText = "🟡 Below Minimum"
                statusColor = getColor(R.color.warning)
                progressColor = getColor(R.color.warning)
            }
            spent <= max -> {
                statusText = "🟢 Within Goal"
                statusColor = getColor(R.color.success_dark)
                progressColor = getColor(R.color.success)
            }
            else -> {
                statusText = "🔴 Over Budget"
                statusColor = getColor(R.color.error)
                progressColor = getColor(R.color.error)
            }
        }

        tvGoalStatus.text = statusText
        tvGoalStatus.setTextColor(statusColor)
        viewGoalProgress.setBackgroundColor(progressColor)

        // Adjust widths based on track size
        viewGoalTrack.post {
            val trackWidth = viewGoalTrack.width
            if (trackWidth > 0) {
                // Progress width ratio (capped at 1.0)
                val progressRatio = (spent / max).coerceIn(0.0, 1.0).toFloat()
                val progressParams = viewGoalProgress.layoutParams
                progressParams.width = (trackWidth * progressRatio).toInt()
                viewGoalProgress.layoutParams = progressParams

                // Min marker position ratio
                val minRatio = (min / max).coerceIn(0.0, 1.0).toFloat()
                val markerParams = viewMinMarker.layoutParams as android.widget.RelativeLayout.LayoutParams
                markerParams.marginStart = (trackWidth * minRatio).toInt()
                viewMinMarker.layoutParams = markerParams
            }
        }
    }
}
