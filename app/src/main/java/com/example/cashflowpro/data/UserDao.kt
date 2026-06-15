package com.example.cashflowpro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM User WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
}
