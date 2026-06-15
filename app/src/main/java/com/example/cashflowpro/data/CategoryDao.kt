package com.example.cashflowpro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM Category")
    suspend fun getAllCategories(): List<Category>

    @androidx.room.Update
    suspend fun updateCategory(category: Category)

    @androidx.room.Delete
    suspend fun deleteCategory(category: Category)
}
