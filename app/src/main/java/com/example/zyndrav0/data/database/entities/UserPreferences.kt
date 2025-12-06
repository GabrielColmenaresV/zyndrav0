package com.example.zyndrav0.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val userId: String,
    val equippedBubbleId: Int? = null,
    val equippedIconId: Int? = null,
    val equippedBackgroundId: Int? = null,
    val equippedAnimationId: Int? = null,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true
)
