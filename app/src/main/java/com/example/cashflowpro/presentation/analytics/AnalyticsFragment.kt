package com.example.cashflowpro.presentation.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.R
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import java.util.Locale

class AnalyticsFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var tvHighestCategory: TextView
    private lateinit var tvAvgDaily: TextView

    private val db by lazy { AppDatabase.getDatabase(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupCharts()
        loadData()
    }

    private fun initViews(view: View) {
        pieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.barChart)
        tvHighestCategory = view.findViewById(R.id.tvHighestCategory)
        tvAvgDaily = view.findViewById(R.id.tvAvgDaily)
    }

    private fun setupCharts() {
        // Pie Chart setup
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.holeRadius = 60f
        pieChart.transparentCircleRadius = 65f
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.legend.isEnabled = false

        // Bar Chart setup
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val expenses = db.expenseDao().getAllExpenses()
                updatePieChart(expenses)
                updateBarChart(expenses)
                updateStats(expenses)
            } catch (e: Exception) {
                android.util.Log.e("AnalyticsFragment", "Error loading analytics", e)
            }
        }
    }

    private fun updatePieChart(expenses: List<Expense>) {
        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = categoryTotals.map { PieEntry(it.value.toFloat(), it.key) }
        
        if (entries.isEmpty()) {
            pieChart.clear()
            return
        }

        val dataSet = PieDataSet(entries, "Spending")
        dataSet.colors = ColorTemplate.VORDIPLOM_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        
        pieChart.data = data
        pieChart.invalidate()
    }

    private fun updateBarChart(expenses: List<Expense>) {
        // Simple monthly summary
        val monthlyTotals = expenses.groupBy { it.date.split("/").getOrNull(1) ?: "01" }
            .mapValues { it.value.sumOf { exp -> exp.amount }.toFloat() }
            .toSortedMap()

        val entries = monthlyTotals.toList().mapIndexed { index, pair -> 
            BarEntry(index.toFloat(), pair.second) 
        }

        if (entries.isEmpty()) {
            barChart.clear()
            return
        }

        val dataSet = BarDataSet(entries, "Monthly")
        dataSet.color = requireContext().getColor(R.color.primary)
        
        val data = BarData(dataSet)
        data.barWidth = 0.6f
        
        barChart.data = data
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(monthlyTotals.keys.toList())
        barChart.invalidate()
    }

    private fun updateStats(expenses: List<Expense>) {
        if (expenses.isEmpty()) return

        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { exp -> exp.amount } }
        
        val topCategory = categoryTotals.maxByOrNull { it.value }
        tvHighestCategory.text = topCategory?.key ?: "N/A"

        val totalAmount = expenses.sumOf { it.amount }
        val avg = totalAmount / 30.0
        tvAvgDaily.text = String.format(Locale.getDefault(), "R%.2f", avg)
    }
}
