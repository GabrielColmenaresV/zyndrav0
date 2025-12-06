package com.example.zyndrav0.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true) val conversationId: Long = 0,
    val userId: String,
    val title: String,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
