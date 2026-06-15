package com.example.cashflowpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val xp: Int = 0,
    val level: Int = 1,
    val profilePhoto: String? = null
)
