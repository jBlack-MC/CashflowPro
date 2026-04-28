package com.example.cashflowpro

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        
        Log.d("AddExpenseActivity", "Activity created")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)

        // Set current date as default
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDate.setText(sdf.format(Date()))
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val amountStr = etAmount.text.toString()
        val date = etDate.text.toString()
        val description = etDescription.text.toString()
        val category = spinnerCategory.selectedItem.toString()

        if (amountStr.isBlank() || date.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            Log.w("AddExpenseActivity", "Attempted to save with empty fields")
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Expense(
            amount = amount,
            category = category,
            description = description,
            date = date,
            startTime = "", // Added empty defaults if required by Entity but not in form
            endTime = "",
            imageUri = null
        )

        lifecycleScope.launch {
            try {
                db.expenseDao().insertExpense(expense)
                Log.i("AddExpenseActivity", "Expense saved: $expense")
                Toast.makeText(this@AddExpenseActivity, "Expense saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e("AddExpenseActivity", "Error saving expense", e)
                Toast.makeText(this@AddExpenseActivity, "Error saving expense", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
