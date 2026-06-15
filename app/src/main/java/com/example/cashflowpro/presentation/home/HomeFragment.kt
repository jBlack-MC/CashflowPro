package com.example.cashflowpro.presentation.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.AddExpenseActivity
import com.example.cashflowpro.HistoryActivity
import com.example.cashflowpro.HistoryAdapter
import com.example.cashflowpro.R
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvTotalBudget: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvPercentUsed: TextView
    private lateinit var tvLeftLabel: TextView
    private lateinit var budgetProgress: LinearProgressIndicator
    
    private lateinit var tvGoalStatus: TextView
    private lateinit var viewGoalProgress: View
    private lateinit var viewGoalTrack: View
    private lateinit var viewMinMarker: View
    private lateinit var tvMinLabel: TextView
    private lateinit var tvMaxLabel: TextView
    
    private lateinit var rvRecentTransactions: RecyclerView
    private lateinit var categoryContainer: LinearLayout
    private lateinit var tvViewAllTransactions: TextView

    private val db by lazy { AppDatabase.getDatabase(requireContext()) }
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardData()
    }

    private fun initViews(view: View) {
        tvGreeting = view.findViewById(R.id.tvGreeting)
        tvTotalBudget = view.findViewById(R.id.tvTotalBudget)
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent)
        tvRemaining = view.findViewById(R.id.tvRemaining)
        tvPercentUsed = view.findViewById(R.id.tvPercentUsed)
        tvLeftLabel = view.findViewById(R.id.tvLeftLabel)
        budgetProgress = view.findViewById(R.id.budgetProgress)

        tvGoalStatus = view.findViewById(R.id.tvGoalStatus)
        viewGoalProgress = view.findViewById(R.id.viewGoalProgress)
        viewGoalTrack = view.findViewById(R.id.viewGoalTrack)
        viewMinMarker = view.findViewById(R.id.viewMinMarker)
        tvMinLabel = view.findViewById(R.id.tvMinLabel)
        tvMaxLabel = view.findViewById(R.id.tvMaxLabel)

        rvRecentTransactions = view.findViewById(R.id.rvRecentTransactions)
        categoryContainer = view.findViewById(R.id.categoryContainer)
        tvViewAllTransactions = view.findViewById(R.id.tvViewAllTransactions)
        
        // Dynamic greeting
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        tvGreeting.text = when (hour) {
            in 0..11 -> "Good Morning,"
            in 12..16 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(emptyList())
        rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        rvRecentTransactions.adapter = adapter
    }

    private fun setupListeners() {
        tvViewAllTransactions.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }
    }

    private fun updateDashboardData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val budget = db.budgetDao().getBudget()
                val minBudget = budget?.min ?: 2000.0
                val maxBudget = budget?.max ?: 5000.0

                val allExpenses = db.expenseDao().getAllExpenses()
                val totalSpent = allExpenses.sumOf { it.amount }

                val allIncome = db.incomeDao().getAllIncome()
                val totalIncome = allIncome.sumOf { it.amount }

                val balance = totalIncome - totalSpent
                val percentUsed = if (maxBudget > 0) (totalSpent / maxBudget * 100).coerceIn(0.0, 100.0) else 0.0

                // Update Overview
                tvTotalBudget.text = String.format(Locale.getDefault(), "R%.0f", totalIncome)
                tvTotalSpent.text = String.format(Locale.getDefault(), "R%.0f", totalSpent)
                tvRemaining.text = String.format(Locale.getDefault(), "R%.2f", balance)
                tvPercentUsed.text = String.format(Locale.getDefault(), "%.0f%%", percentUsed)
                tvLeftLabel.text = "spent of monthly budget"

                // Update Progress Indicator
                budgetProgress.progress = percentUsed.toInt()

                // Update Recent Transactions
                adapter.updateData(allExpenses.take(5))

                // Update Categories
                updateCategorySummary(allExpenses)
                
                // Update Health Visuals
                updateGoalRangeVisuals(totalSpent, minBudget, maxBudget)

            } catch (e: Exception) {
                Log.e("HomeFragment", "Error updating dashboard", e)
            }
        }
    }

    private fun updateCategorySummary(expenses: List<Expense>) {
        categoryContainer.removeAllViews()
        
        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .take(3)

        if (categoryTotals.isEmpty()) {
            val tvEmpty = TextView(requireContext())
            tvEmpty.text = "No transactions logged yet"
            tvEmpty.setTextColor(requireContext().getColor(R.color.text_hint))
            categoryContainer.addView(tvEmpty)
            return
        }

        for ((category, total) in categoryTotals) {
            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(0, 12, 0, 12)
            
            val tvName = TextView(requireContext())
            tvName.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            tvName.text = getCategoryEmoji(category) + " " + category
            tvName.setTextColor(requireContext().getColor(R.color.text_primary))
            tvName.textSize = 15f
            
            val tvTotal = TextView(requireContext())
            tvTotal.text = String.format(Locale.getDefault(), "R%.0f", total)
            tvTotal.setTypeface(null, Typeface.BOLD)
            tvTotal.setTextColor(requireContext().getColor(R.color.text_primary))
            tvTotal.textSize = 15f
            
            row.addView(tvName)
            row.addView(tvTotal)
            categoryContainer.addView(row)
        }
    }

    private fun getCategoryEmoji(category: String): String {
        return when(category.lowercase()) {
            "food", "groceries" -> "🍞"
            "transport" -> "🚗"
            "entertainment" -> "🍿"
            "education" -> "🎓"
            "clothing" -> "👕"
            "utilities" -> "💡"
            "savings" -> "💰"
            else -> "📁"
        }
    }

    private fun updateGoalRangeVisuals(spent: Double, min: Double, max: Double) {
        tvMinLabel.text = String.format(Locale.getDefault(), "Min R%.0f", min)
        tvMaxLabel.text = String.format(Locale.getDefault(), "Max R%.0f", max)

        val statusText: String
        val statusColor: Int
        val progressColor: Int

        when {
            spent < min -> {
                statusText = "🟡 Below Minimum"
                statusColor = requireContext().getColor(R.color.warning)
                progressColor = requireContext().getColor(R.color.warning)
            }
            spent <= max -> {
                statusText = "🟢 Within Goal"
                statusColor = requireContext().getColor(R.color.success_dark)
                progressColor = requireContext().getColor(R.color.success)
            }
            else -> {
                statusText = "🔴 Over Budget"
                statusColor = requireContext().getColor(R.color.error)
                progressColor = requireContext().getColor(R.color.error)
            }
        }

        tvGoalStatus.text = statusText
        tvGoalStatus.setTextColor(statusColor)
        viewGoalProgress.setBackgroundColor(progressColor)

        viewGoalTrack.post {
            val trackWidth = viewGoalTrack.width
            if (trackWidth > 0) {
                val progressRatio = (spent / max).coerceIn(0.0, 1.2).toFloat()
                val progressParams = viewGoalProgress.layoutParams
                progressParams.width = (trackWidth * (progressRatio.coerceAtMost(1.0f))).toInt()
                viewGoalProgress.layoutParams = progressParams

                val minRatio = (min / max).coerceIn(0.0, 1.0).toFloat()
                val markerParams = viewMinMarker.layoutParams as android.widget.RelativeLayout.LayoutParams
                markerParams.marginStart = (trackWidth * minRatio).toInt()
                viewMinMarker.layoutParams = markerParams
            }
        }
    }
}
