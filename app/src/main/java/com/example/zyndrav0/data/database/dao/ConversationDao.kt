package com.example.zyndrav0.data.database.dao

import androidx.room.*
import com.example.zyndrav0.data.database.entities.Conversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE userId = :userId AND isArchived = 0 ORDER BY lastMessageTime DESC")
    fun getAllConversations(userId: String): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE conversationId = :conversationId")
    fun getConversationById(conversationId: Long): Flow<Conversation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation): Long

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Query("UPDATE conversations SET lastMessage = :message, lastMessageTime = :timestamp WHERE conversationId = :conversationId")
    suspend fun updateLastMessage(conversationId: Long, message: String, timestamp: Long)

    @Query("UPDATE conversations SET isArchived = 1 WHERE conversationId = :conversationId")
    suspend fun archiveConversation(conversationId: Long)

    @Delete
    suspend fun deleteConversation(conversation: Conversation)

    @Query("DELETE FROM conversations WHERE conversationId = :conversationId")
    suspend fun deleteConversationById(conversationId: Long)
}
