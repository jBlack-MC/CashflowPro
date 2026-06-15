package com.example.cashflowpro.presentation.expenses

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.AddExpenseActivity
import com.example.cashflowpro.HistoryAdapter
import com.example.cashflowpro.R
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ExpensesFragment : Fragment() {

    private lateinit var rvExpenses: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var emptyState: View

    private val db by lazy { AppDatabase.getDatabase(requireContext()) }
    private lateinit var adapter: HistoryAdapter
    private var allExpenses: List<Expense> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecyclerView()
        setupSearch()
        setupFilters()
        loadExpenses()
    }

    private fun initViews(view: View) {
        rvExpenses = view.findViewById(R.id.rvExpenses)
        etSearch = view.findViewById(R.id.etSearch)
        filterChipGroup = view.findViewById(R.id.filterChipGroup)
        emptyState = view.findViewById(R.id.emptyState)
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(emptyList()) { expense ->
            // Handle details view
        }
        rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        rvExpenses.adapter = adapter

        // Swipe to Delete
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expense = adapter.getExpenseAt(position)
                
                if (direction == ItemTouchHelper.LEFT) {
                    deleteExpense(expense, position)
                } else {
                    // Edit action - currently reusing AddExpenseActivity for simplicity or placeholder
                    val intent = Intent(requireContext(), AddExpenseActivity::class.java)
                    intent.putExtra("EXPENSE_ID", expense.id)
                    startActivity(intent)
                    adapter.notifyItemChanged(position)
                }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(rvExpenses)
    }

    private fun deleteExpense(expense: Expense, position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            db.expenseDao().deleteExpense(expense)
            Snackbar.make(rvExpenses, "Expense deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    viewLifecycleOwner.lifecycleScope.launch {
                        db.expenseDao().insertExpense(expense)
                        loadExpenses()
                    }
                }.show()
            loadExpenses()
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterExpenses()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilters() {
        filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            filterExpenses()
        }
    }

    private fun loadExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            allExpenses = db.expenseDao().getAllExpenses()
            filterExpenses()
        }
    }

    private fun filterExpenses() {
        val query = etSearch.text.toString().lowercase()
        val checkedChipId = filterChipGroup.checkedChipId
        
        val categoryFilter = when (checkedChipId) {
            R.id.chipFood -> "food"
            R.id.chipTransport -> "transport"
            R.id.chipShopping -> "shopping"
            else -> null
        }

        val filtered = allExpenses.filter {
            val matchesQuery = it.title.lowercase().contains(query) || it.category.lowercase().contains(query)
            val matchesCategory = categoryFilter == null || it.category.lowercase() == categoryFilter
            matchesQuery && matchesCategory
        }

        adapter.updateData(filtered)
        emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }
}
