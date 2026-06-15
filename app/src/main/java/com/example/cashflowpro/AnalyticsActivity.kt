package com.example.cashflowpro

import android.app.DatePickerDialog
import android.util.Log
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnalyticsActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }
    private lateinit var pieChart: PieChart
    private lateinit var tvTotalValue: TextView
    private lateinit var tvInsight: TextView
    private val dbSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)
        
        setupToolbar()
        initViews()
        setupFilters()
        
        // Initial load (This Month)
        loadDataForRange(getStartOfMonth(), Date())
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        pieChart = findViewById(R.id.pieChart)
        tvTotalValue = findViewById(R.id.tvTotalValue)
        tvInsight = findViewById(R.id.tvInsight)
        
        setupPieChart()
    }

    private fun setupPieChart() {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400)
        pieChart.legend.isEnabled = true
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
    }

    private fun setupFilters() {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupFilters)
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipToday -> loadDataForRange(Date(), Date())
                R.id.chipWeek -> loadDataForRange(getStartOfWeek(), Date())
                R.id.chipMonth -> loadDataForRange(getStartOfMonth(), Date())
                R.id.chipCustom -> showDateRangePicker()
            }
        }
    }

    private fun loadDataForRange(startDate: Date, endDate: Date) {
        lifecycleScope.launch {
            val allExpenses = db.expenseDao().getAllExpenses()
            val filteredExpenses = allExpenses.filter {
                val expenseDate = try { dbSdf.parse(it.date) } catch(e: Exception) { null }
                expenseDate != null && (!expenseDate.before(startDate)) && (!expenseDate.after(endDate))
            }

            updateChartWithExpenses(filteredExpenses)
        }
    }

    private fun updateChartWithExpenses(expenses: List<Expense>) {
        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = categoryTotals.map { PieEntry(it.value.toFloat(), it.key) }
        
        if (entries.isEmpty()) {
            pieChart.clear()
            tvTotalValue.text = "R0.00"
            tvInsight.text = "No expenses found for this period."
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)

        pieChart.data = data
        pieChart.highlightValues(null)
        pieChart.invalidate()
        pieChart.animateY(1000)
        Log.d("Graph", "Graph Generated with ${entries.size} categories")

        val total = categoryTotals.values.sum()
        tvTotalValue.text = String.format(Locale.getDefault(), "R%.2f", total)
        
        val topCategory = categoryTotals.maxByOrNull { it.value }
        tvInsight.text = String.format(Locale.getDefault(), "Your biggest expense was %s (R%.2f).", topCategory?.key, topCategory?.value)
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val start = Calendar.getInstance()
            start.set(year, month, day)
            
            DatePickerDialog(this, { _, eYear, eMonth, eDay ->
                val end = Calendar.getInstance()
                end.set(eYear, eMonth, eDay)
                loadDataForRange(start.time, end.time)
            }, year, month, day).apply {
                setTitle("Select End Date")
                show()
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply {
            setTitle("Select Start Date")
            show()
        }
    }

    private fun getStartOfWeek(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        return cal.time
    }

    private fun getStartOfMonth(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal.time
    }
}
