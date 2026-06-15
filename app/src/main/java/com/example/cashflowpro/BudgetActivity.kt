package com.example.cashflowpro

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Budget
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class BudgetActivity : AppCompatActivity() {

    private lateinit var etMaxBudget: EditText
    private lateinit var etMinGoal: EditText
    private lateinit var btnSave: MaterialButton
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        setupToolbar()
        initViews()
        loadCurrentBudget()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        etMaxBudget = findViewById(R.id.etMaxBudget)
        etMinGoal = findViewById(R.id.etMinGoal)
        btnSave = findViewById(R.id.btnSaveSettings)

        btnSave.setOnClickListener {
            saveBudget()
        }
    }

    private fun loadCurrentBudget() {
        lifecycleScope.launch {
            val budget = db.budgetDao().getBudget()
            if (budget != null) {
                etMaxBudget.setText(budget.max.toString())
                etMinGoal.setText(budget.min.toString())
            }
        }
    }

    private fun saveBudget() {
        val maxStr = etMaxBudget.text.toString().trim()
        val minStr = etMinGoal.text.toString().trim()

        if (maxStr.isEmpty() || minStr.isEmpty()) {
            Toast.makeText(this, "Enter both limits", Toast.LENGTH_SHORT).show()
            return
        }

        val max = maxStr.toDoubleOrNull() ?: 0.0
        val min = minStr.toDoubleOrNull() ?: 0.0

        lifecycleScope.launch {
            val budget = Budget(id = 1, min = min, max = max)
            db.budgetDao().insertBudget(budget)
            Toast.makeText(this@BudgetActivity, "Strategy updated!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
