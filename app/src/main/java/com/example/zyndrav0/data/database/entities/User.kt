package com.example.zyndrav0.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val email: String,
    val username: String,
    val passwordHash: String, // Campo para la contraseña hasheada nice
    val profileImageUri: String? = null,
    val currency: Int = 500, // Monedas iniciales para los pobres
    val level: Int = 1,
    val experience: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)
