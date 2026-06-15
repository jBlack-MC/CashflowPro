package com.example.cashflowpro

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.data.AppDatabase
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var etSearch: EditText
    private lateinit var btnFilter: Button
    private lateinit var adapter: HistoryAdapter
    private lateinit var emptyState: android.view.View

    private var allExpenses: List<com.example.cashflowpro.data.Expense> = emptyList()

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        
        Log.d("HistoryActivity", "History screen opened")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        loadAllExpenses()
        setupToolbar()

        btnFilter.setOnClickListener {
            filterExpenses()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        rvHistory = findViewById(R.id.rvHistory)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        etSearch = findViewById(R.id.etSearch)
        btnFilter = findViewById(R.id.btnFilter)
        emptyState = findViewById(R.id.emptyState)

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBySearch(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun filterBySearch(query: String) {
        val filtered = allExpenses.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.category.contains(query, ignoreCase = true) ||
            (it.notes?.contains(query, ignoreCase = true) ?: false)
        }
        updateUI(filtered, isFiltering = true)
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(emptyList())
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }

    private fun loadAllExpenses() {
        lifecycleScope.launch {
            try {
                allExpenses = db.expenseDao().getAllExpenses()
                Log.i("HistoryActivity", "Loaded ${allExpenses.size} expenses")
                updateUI(allExpenses)
            } catch (e: Exception) {
                Log.e("HistoryActivity", "Error loading expenses", e)
            }
        }
    }

    private fun updateUI(expenses: List<com.example.cashflowpro.data.Expense>, isFiltering: Boolean = false) {
        adapter.updateData(expenses)
        if (expenses.isEmpty()) {
            rvHistory.visibility = android.view.View.GONE
            emptyState.visibility = android.view.View.VISIBLE
            if (isFiltering) {
                findViewById<TextView>(R.id.emptyStateText).text = "No results for your search"
            } else {
                findViewById<TextView>(R.id.emptyStateText).text = "No expenses found"
            }
        } else {
            rvHistory.visibility = android.view.View.VISIBLE
            emptyState.visibility = android.view.View.GONE
        }
    }

    private fun filterExpenses() {
        val start = etStartDate.text.toString()
        val end = etEndDate.text.toString()

        if (start.isBlank() || end.isBlank()) {
            Toast.makeText(this, "Please enter both dates (YYYY-MM-DD)", Toast.LENGTH_SHORT).show()
            Log.w("HistoryActivity", "Filter attempt with empty dates")
            return
        }

        lifecycleScope.launch {
            try {
                Log.d("HistoryActivity", "Filtering from $start to $end")
                val filtered = db.expenseDao().getExpensesByDateRange(start, end)
                allExpenses = filtered // Update base list for search within date range
                updateUI(filtered)
                Log.i("HistoryActivity", "Found ${filtered.size} expenses in range")
            } catch (e: Exception) {
                Log.e("HistoryActivity", "Filter error", e)
                Toast.makeText(this@HistoryActivity, "Invalid date format. Use YYYY-MM-DD", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
