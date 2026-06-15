package com.example.cashflowpro

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.util.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var tvReportMonth: TextView
    private lateinit var tvTotalSpending: TextView
    private lateinit var tvHighestCategory: TextView
    private lateinit var tvLowestCategory: TextView
    private lateinit var tvMoneySaved: TextView
    private lateinit var btnExportPdf: MaterialButton

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

        btnExportPdf.setOnClickListener {
            Toast.makeText(this, "Generating PDF Report...", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<MaterialButton>(R.id.btnShare).setOnClickListener {
            Toast.makeText(this, "Sharing Report...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateReport() {
        lifecycleScope.launch {
            val expenses = db.expenseDao().getAllExpenses()
            val income = db.incomeDao().getAllIncome()
            
            val totalExpenses = expenses.sumOf { it.amount }
            val totalIncome = income.sumOf { it.amount }
            val netSavings = totalIncome - totalExpenses

            val categoryGroup = expenses.groupBy { it.category }
                .mapValues { it.value.sumOf { exp -> exp.amount } }

            val highest = categoryGroup.maxByOrNull { it.value }
            val lowest = categoryGroup.minByOrNull { it.value }

            tvTotalSpending.text = String.format(Locale.getDefault(), "Total Expenses: R%.2f", totalExpenses)
            tvHighestCategory.text = String.format(Locale.getDefault(), "Highest Category: %s (R%.0f)", highest?.key ?: "N/A", highest?.value ?: 0.0)
            tvLowestCategory.text = String.format(Locale.getDefault(), "Lowest Category: %s (R%.0f)", lowest?.key ?: "N/A", lowest?.value ?: 0.0)
            tvMoneySaved.text = String.format(Locale.getDefault(), "Net Savings: R%.2f", netSavings)
        }
    }
}
