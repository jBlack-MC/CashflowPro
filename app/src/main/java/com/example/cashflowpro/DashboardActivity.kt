package com.example.cashflowpro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import kotlinx.coroutines.launch
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var btnAddExpense: Button
    private lateinit var btnHistory: Button
    private lateinit var btnGraphs: Button
    
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvBudgetUsedAmount: TextView
    private lateinit var tvPercentUsed: TextView
    private lateinit var tvLeftLabel: TextView
    
    private lateinit var tvGroceriesSpent: TextView
    private lateinit var tvTransportSpent: TextView
    private lateinit var tvEntertainmentSpent: TextView
    
    private lateinit var viewProgressBar: View
    private lateinit var viewProgressRemaining: View

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val totalMonthlyBudget = 5000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        
        Log.d("DashboardActivity", "Dashboard activity created")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        Log.i("DashboardActivity", "Refreshing dashboard data")
        updateDashboardData()
    }

    private fun initViews() {
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnHistory = findViewById(R.id.btnHistory)
        btnGraphs = findViewById(R.id.btnGraphs)
        
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvRemaining = findViewById(R.id.tvRemaining)
        tvBudgetUsedAmount = findViewById(R.id.tvBudgetUsedAmount)
        tvPercentUsed = findViewById(R.id.tvPercentUsed)
        tvLeftLabel = findViewById(R.id.tvLeftLabel)
        
        tvGroceriesSpent = findViewById(R.id.tvGroceriesSpent)
        tvTransportSpent = findViewById(R.id.tvTransportSpent)
        tvEntertainmentSpent = findViewById(R.id.tvEntertainmentSpent)
        
        viewProgressBar = findViewById(R.id.viewProgressBar)
        viewProgressRemaining = findViewById(R.id.viewProgressRemaining)
    }

    private fun setupListeners() {
        btnAddExpense.setOnClickListener {
            Log.d("DashboardActivity", "Navigating to AddExpense")
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        btnHistory.setOnClickListener {
            Log.d("DashboardActivity", "Navigating to History")
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        btnGraphs.setOnClickListener {
            Log.d("DashboardActivity", "Navigating to Analytics")
            startActivity(Intent(this, AnalyticsActivity::class.java))
        }
    }

    private fun updateDashboardData() {
        lifecycleScope.launch {
            try {
                val totalSpent = db.expenseDao().getTotalExpenses() ?: 0.0
                val groceriesSpent = db.expenseDao().getTotalByCategory("Groceries") ?: 0.0
                val transportSpent = db.expenseDao().getTotalByCategory("Transport") ?: 0.0
                val entertainmentSpent = db.expenseDao().getTotalByCategory("Entertainment") ?: 0.0

                val remaining = totalMonthlyBudget - totalSpent
                val percentUsed = (totalSpent / totalMonthlyBudget * 100).coerceIn(0.0, 100.0)

                // Update Overview
                tvTotalSpent.text = String.format(Locale.getDefault(), "R%.0f", totalSpent)
                tvRemaining.text = String.format(Locale.getDefault(), "R%.0f", remaining)
                tvBudgetUsedAmount.text = String.format(Locale.getDefault(), "R%.0f", totalSpent)
                tvPercentUsed.text = String.format(Locale.getDefault(), "%.0f%% used", percentUsed)
                tvLeftLabel.text = String.format(Locale.getDefault(), "✓ R%.0f left", remaining)

                // Update Categories
                tvGroceriesSpent.text = String.format(Locale.getDefault(), "R%.0f", groceriesSpent)
                tvTransportSpent.text = String.format(Locale.getDefault(), "R%.0f", transportSpent)
                tvEntertainmentSpent.text = String.format(Locale.getDefault(), "R%.0f", entertainmentSpent)

                // Update Progress Bar (Simulate weight)
                val params = viewProgressBar.layoutParams as android.widget.LinearLayout.LayoutParams
                params.weight = percentUsed.toFloat()
                viewProgressBar.layoutParams = params

                val remainParams = viewProgressRemaining.layoutParams as android.widget.LinearLayout.LayoutParams
                remainParams.weight = (100 - percentUsed).toFloat()
                viewProgressRemaining.layoutParams = remainParams
                
                Log.d("DashboardActivity", "Dashboard updated. Total spent: R$totalSpent")

            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error updating dashboard", e)
            }
        }
    }
}
