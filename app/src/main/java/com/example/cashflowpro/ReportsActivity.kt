package com.example.cashflowpro

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportsActivity : AppCompatActivity() {

    private lateinit var tvReportMonth: TextView
    private lateinit var tvTotalSpending: TextView
    private lateinit var tvHighestCategory: TextView
    private lateinit var tvLowestCategory: TextView
    private lateinit var tvMoneySaved: TextView
    private lateinit var btnExportPdf: Button
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        setupToolbar()
        initViews()
        generateReport()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        tvReportMonth = findViewById(R.id.tvReportMonth)
        tvTotalSpending = findViewById(R.id.tvTotalSpending)
        tvHighestCategory = findViewById(R.id.tvHighestCategory)
        tvLowestCategory = findViewById(R.id.tvLowestCategory)
        tvMoneySaved = findViewById(R.id.tvMoneySaved)
        btnExportPdf = findViewById(R.id.btnExportPdf)

        val cal = Calendar.getInstance()
        val monthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
        tvReportMonth.text = "$monthYear Summary"

        btnExportPdf.setOnClickListener {
            Toast.makeText(this, "PDF Export Feature Coming Soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateReport() {
        lifecycleScope.launch {
            val expenses = db.expenseDao().getAllExpenses()
            val budget = db.budgetDao().getBudget()
            
            if (expenses.isEmpty()) {
                Toast.makeText(this@ReportsActivity, "No data to report", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val totalSpent = expenses.sumOf { it.amount }
            val categoryTotals = expenses.groupBy { it.category }
                .mapValues { it.value.sumOf { v -> v.amount } }

            val highest = categoryTotals.maxByOrNull { it.value }
            val lowest = categoryTotals.minByOrNull { it.value }
            
            val maxBudget = budget?.max ?: 5000.0
            val saved = (maxBudget - totalSpent).coerceAtLeast(0.0)

            tvTotalSpending.text = String.format(Locale.getDefault(), "Total Spending: R%.2f", totalSpent)
            tvHighestCategory.text = String.format(Locale.getDefault(), "Highest Category: %s (R%.2f)", highest?.key ?: "N/A", highest?.value ?: 0.0)
            tvLowestCategory.text = String.format(Locale.getDefault(), "Lowest Category: %s (R%.2f)", lowest?.key ?: "N/A", lowest?.value ?: 0.0)
            tvMoneySaved.text = String.format(Locale.getDefault(), "Money Saved: R%.2f", saved)
        }
    }
}
