package com.example.cashflowpro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Budget
import kotlinx.coroutines.launch

class BudgetActivity : AppCompatActivity() {

    private lateinit var etMaxBudget: EditText
    private lateinit var etMinGoal: EditText
    private lateinit var btnSaveSettings: Button
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget)

        initViews()
        loadBudgetData()
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
        etMaxBudget = findViewById(R.id.etMaxBudget)
        etMinGoal = findViewById(R.id.etMinGoal)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadBudgetData() {
        lifecycleScope.launch {
            val budget = db.budgetDao().getBudget()
            budget?.let {
                etMaxBudget.setText(it.max.toString())
                etMinGoal.setText(it.min.toString())
            }
        }
    }

    private fun setupListeners() {
        btnSaveSettings.setOnClickListener {
            val max = etMaxBudget.text.toString().toDoubleOrNull() ?: 0.0
            val min = etMinGoal.text.toString().toDoubleOrNull() ?: 0.0

            lifecycleScope.launch {
                val budget = db.budgetDao().getBudget()
                val newBudget = budget?.copy(min = min, max = max) ?: Budget(min = min, max = max)
                db.budgetDao().insertBudget(newBudget)
                Toast.makeText(this@BudgetActivity, "Settings Saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
