package com.example.cashflowpro

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import kotlinx.coroutines.launch

import com.google.android.material.textfield.TextInputEditText

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var etSearch: TextInputEditText
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: HistoryAdapter
    private var allExpenses: List<Expense> = emptyList()

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setupToolbar()
        initViews()
        setupRecyclerView()
        setupSearch()
        loadData()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        rvHistory = findViewById(R.id.rvHistory)
        etSearch = findViewById(R.id.etSearch)
        emptyState = findViewById(R.id.emptyState)
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(emptyList())
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterData(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                allExpenses = db.expenseDao().getAllExpenses()
                adapter.updateData(allExpenses)
                updateEmptyState(allExpenses.isEmpty())
            } catch (e: Exception) {
                android.util.Log.e("HistoryActivity", "Error loading data", e)
            }
        }
    }

    private fun filterData(query: String) {
        val filtered = allExpenses.filter {
            it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
        adapter.updateData(filtered)
        updateEmptyState(filtered.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}
