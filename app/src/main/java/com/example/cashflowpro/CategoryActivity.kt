package com.example.cashflowpro

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Category
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {
    private val TAG = "CategoryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val etCategoryName = findViewById<EditText>(R.id.etCategoryName)
        val btnSaveCategory = findViewById<Button>(R.id.btnSaveCategory)

        val db = AppDatabase.getDatabase(this)

        btnSaveCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                lifecycleScope.launch {
                    db.categoryDao().insertCategory(Category(name = name))
                    Log.d(TAG, "Category saved: $name")
                    Toast.makeText(this@CategoryActivity, "Category added!", Toast.LENGTH_SHORT).show()
                    etCategoryName.text.clear()
                }
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
