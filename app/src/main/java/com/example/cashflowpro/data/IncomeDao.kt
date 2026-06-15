package com.example.cashflowpro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income)

    @Query("SELECT * FROM Income ORDER BY date DESC")
    suspend fun getAllIncome(): List<Income>

    @Query("SELECT SUM(amount) FROM Income")
    suspend fun getTotalIncome(): Double?
}
