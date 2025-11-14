package com.example.zyndrav0.data.database.dao

import androidx.room.*
import com.example.zyndrav0.data.database.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET currency = :currency WHERE userId = :userId")
    suspend fun updateCurrency(userId: String, currency: Int)

    @Query("UPDATE users SET level = :level, experience = :experience WHERE userId = :userId")
    suspend fun updateLevelAndExperience(userId: String, level: Int, experience: Int)

    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE userId = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long)

    @Query("UPDATE users SET profileImageUri = :uri WHERE userId = :userId")
    suspend fun updateProfileImage(userId: String, uri: String?)

    @Delete
    suspend fun deleteUser(user: User)
}
