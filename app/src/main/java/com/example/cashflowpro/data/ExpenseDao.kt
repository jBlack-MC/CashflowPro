package com.example.cashflowpro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM Expense ORDER BY date DESC")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT SUM(amount) FROM Expense")
    suspend fun getTotalExpenses(): Double?

    @Query("SELECT SUM(amount) FROM Expense WHERE category = :category")
    suspend fun getTotalByCategory(category: String): Double?

    @Query("SELECT * FROM Expense WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesByDateRange(startDate: String, endDate: String): List<Expense>

    @Query("SELECT * FROM Expense WHERE category = :category ORDER BY date DESC")
    suspend fun getExpensesByCategory(category: String): List<Expense>
}
