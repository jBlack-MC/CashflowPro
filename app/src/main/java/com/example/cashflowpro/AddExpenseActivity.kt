package com.example.cashflowpro

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        
        initViews()
        loadCategories()
        setupListeners()
        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
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
            val categories = db.categoryDao().getAllCategories().map { it.name }
            if (categories.isEmpty()) {
                // If empty, maybe add some defaults or prompt user
                val defaults = listOf("Food", "Transport", "Entertainment", "Education", "Clothing", "Utilities", "Savings")
                val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, defaults)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
            } else {
                val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
            }
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
        val category = spinnerCategory.selectedItem?.toString() ?: ""

        if (title.isEmpty() || amountStr.isEmpty() || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Expense(
            title = title,
            amount = amount,
            category = category,
            date = date,
            notes = if (notes.isEmpty()) null else notes
        )

        lifecycleScope.launch {
            try {
                db.expenseDao().insertExpense(expense)
                Log.d("Expense", "Expense Added: $title, Amount: $amount")
                Toast.makeText(this@AddExpenseActivity, "Expense saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e("Database", "Insert Failed", e)
                Toast.makeText(this@AddExpenseActivity, "Error saving expense", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
