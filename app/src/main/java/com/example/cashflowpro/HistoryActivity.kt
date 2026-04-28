package com.example.cashflowpro

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
    private lateinit var btnFilter: Button
    private lateinit var adapter: HistoryAdapter

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

        btnFilter.setOnClickListener {
            filterExpenses()
        }
    }

    private fun initViews() {
        rvHistory = findViewById(R.id.rvHistory)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        btnFilter = findViewById(R.id.btnFilter)
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(emptyList())
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }

    private fun loadAllExpenses() {
        lifecycleScope.launch {
            try {
                val expenses = db.expenseDao().getAllExpenses()
                Log.i("HistoryActivity", "Loaded ${expenses.size} expenses")
                adapter.updateData(expenses)
            } catch (e: Exception) {
                Log.e("HistoryActivity", "Error loading expenses", e)
            }
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
                adapter.updateData(filtered)
                Log.i("HistoryActivity", "Found ${filtered.size} expenses in range")
                if (filtered.isEmpty()) {
                    Toast.makeText(this@HistoryActivity, "No transactions found for these dates", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("HistoryActivity", "Filter error", e)
                Toast.makeText(this@HistoryActivity, "Invalid date format. Use YYYY-MM-DD", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
