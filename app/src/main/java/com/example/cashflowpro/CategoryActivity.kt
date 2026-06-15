package com.example.cashflowpro

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Category
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var btnSaveCategory: MaterialButton
    private lateinit var categoriesListContainer: LinearLayout
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        setupToolbar()
        initViews()
        loadCategories()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        etCategoryName = findViewById(R.id.etCategoryName)
        btnSaveCategory = findViewById(R.id.btnSaveCategory)
        categoriesListContainer = findViewById(R.id.categoriesListContainer)

        btnSaveCategory.setOnClickListener {
            saveCategory()
        }
    }

    private fun saveCategory() {
        val name = etCategoryName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            db.categoryDao().insertCategory(Category(name = name))
            etCategoryName.text.clear()
            loadCategories()
            Toast.makeText(this@CategoryActivity, "Category added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val categories = db.categoryDao().getAllCategories()
            categoriesListContainer.removeAllViews()
            val inflater = LayoutInflater.from(this@CategoryActivity)

            if (categories.isEmpty()) {
                val tv = TextView(this@CategoryActivity)
                tv.text = "No custom categories yet"
                tv.setPadding(32, 32, 32, 32)
                categoriesListContainer.addView(tv)
                return@launch
            }

            for (cat in categories) {
                val view = inflater.inflate(android.R.layout.simple_list_item_1, categoriesListContainer, false)
                val tv = view.findViewById<TextView>(android.R.id.text1)
                tv.text = "📁 " + cat.name
                tv.setTextColor(getColor(R.color.text_primary))
                tv.setPadding(48, 48, 48, 48)
                categoriesListContainer.addView(view)
            }
        }
    }
}
