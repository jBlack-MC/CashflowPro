package com.example.cashflowpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Badge(
    @PrimaryKey val id: String, // e.g., "beginner_saver"
    val name: String,
    val description: String,
    val icon: String, // Emoji or drawable name
    val isUnlocked: Boolean = false,
    val unlockDate: String? = null
)
