package com.example.cashflowpro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavingsGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoal)

    @Query("SELECT * FROM SavingsGoal")
    suspend fun getAllGoals(): List<SavingsGoal>

    @Query("SELECT SUM(currentAmount) FROM SavingsGoal")
    suspend fun getTotalSavings(): Double?
}
