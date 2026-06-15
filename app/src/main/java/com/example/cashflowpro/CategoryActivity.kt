package com.example.cashflowpro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Category
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var btnSaveCategory: Button
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
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                saveCategory(name)
            } else {
                Toast.makeText(this, "Enter category name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveCategory(name: String) {
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
            
            if (categories.isEmpty()) {
                val defaults = listOf("Food", "Transport", "Entertainment", "Education", "Clothing", "Utilities", "Savings")
                defaults.forEach { db.categoryDao().insertCategory(Category(name = it)) }
                loadCategories() // Reload after inserting defaults
                return@launch
            }

            refreshCategoryList(categories)
        }
    }

    private fun refreshCategoryList(categories: List<Category>) {
        categoriesListContainer.removeAllViews()

        for (category in categories) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_category, categoriesListContainer, false)
            val tvName = itemView.findViewById<TextView>(R.id.tvCategoryName)
            val btnDelete = itemView.findViewById<ImageView>(R.id.btnDelete)
            val btnEdit = itemView.findViewById<ImageView>(R.id.btnEdit)

            tvName.text = category.name
            
            btnDelete.setOnClickListener {
                lifecycleScope.launch {
                    db.categoryDao().deleteCategory(category)
                    loadCategories()
                }
            }

            btnEdit.setOnClickListener {
                showEditDialog(category)
            }

            categoriesListContainer.addView(itemView)
        }
    }

    private fun showEditDialog(category: Category) {
        val editText = EditText(this)
        editText.setText(category.name)
        AlertDialog.Builder(this)
            .setTitle("Edit Category")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    lifecycleScope.launch {
                        db.categoryDao().updateCategory(category.copy(name = newName))
                        loadCategories()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
