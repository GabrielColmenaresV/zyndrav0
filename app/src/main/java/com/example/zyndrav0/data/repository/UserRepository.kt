package com.example.zyndrav0.data.repository

import com.example.zyndrav0.data.database.dao.UserDao
import com.example.zyndrav0.data.database.dao.UserPreferencesDao
import com.example.zyndrav0.data.database.entities.User
import com.example.zyndrav0.data.database.entities.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import java.util.UUID

class UserRepository(
    private val userDao: UserDao,
    private val userPreferencesDao: UserPreferencesDao
) {

    fun getUserById(userId: String): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun createUser(email: String, username: String, password: String): User {
        val userId = UUID.randomUUID().toString()
        val user = User(
            userId = userId,
            email = email,
            username = username,
            passwordHash = hashPassword(password) // Hashear la contraseña
        )
        userDao.insertUser(user)

        val preferences = UserPreferences(userId = userId)
        userPreferencesDao.insertPreferences(preferences)

        return user
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun updateProfileImage(userId: String, uri: String?) {
        userDao.updateProfileImage(userId, uri)
    }

    suspend fun updateCurrency(userId: String, currency: Int) {
        userDao.updateCurrency(userId, currency)
    }

    suspend fun addCurrency(userId: String, amount: Int) {
        val user = userDao.getUserById(userId).first() ?: return
        userDao.updateCurrency(userId, user.currency + amount)
    }

    suspend fun updateLastLogin(userId: String) {
        userDao.updateLastLogin(userId, System.currentTimeMillis())
    }

    fun getUserPreferences(userId: String): Flow<UserPreferences?> {
        return userPreferencesDao.getPreferences(userId)
    }

    suspend fun updateUserPreferences(preferences: UserPreferences) {
        userPreferencesDao.updatePreferences(preferences)
    }

    suspend fun updateDarkMode(userId: String, isDark: Boolean) {
        userPreferencesDao.updateDarkMode(userId, isDark)
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }
}
