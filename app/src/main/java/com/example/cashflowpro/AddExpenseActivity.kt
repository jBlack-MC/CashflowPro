package com.example.cashflowpro

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etNotes: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        
        setupToolbar()
        initViews()
        loadCategories()
        setupListeners()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etNotes = findViewById(R.id.etNotes)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etDate.setText(sdf.format(Date()))
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val dbCategories = db.categoryDao().getAllCategories().map { it.name }
            val defaults = listOf("Food", "Transport", "Entertainment", "Shopping", "Health", "Education", "Utilities", "Savings")
            
            val allCategories = if (dbCategories.isEmpty()) defaults else (defaults + dbCategories).distinct()
            
            val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, allCategories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val title = etTitle.text.toString().trim()
        val amountStr = etAmount.text.toString().trim()
        val date = etDate.text.toString().trim()
        val notes = etNotes.text.toString().trim()
        val category = spinnerCategory.selectedItem?.toString() ?: "Other"

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val expense = Expense(
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    notes = if (notes.isEmpty()) null else notes
                )
                db.expenseDao().insertExpense(expense)
                Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@AddExpenseActivity, "Error saving expense", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
