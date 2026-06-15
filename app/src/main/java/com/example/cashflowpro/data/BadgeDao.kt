package com.example.cashflowpro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: Badge)

    @Query("SELECT * FROM Badge")
    suspend fun getAllBadges(): List<Badge>

    @Query("SELECT * FROM Badge WHERE id = :id LIMIT 1")
    suspend fun getBadgeById(id: String): Badge?
}
