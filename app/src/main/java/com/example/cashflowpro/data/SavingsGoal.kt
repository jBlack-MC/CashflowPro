package com.example.cashflowpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val dueDate: String
)
