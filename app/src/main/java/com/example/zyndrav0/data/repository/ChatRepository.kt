package com.example.zyndrav0.data.repository

import com.example.zyndrav0.data.database.dao.ConversationDao
import com.example.zyndrav0.data.database.dao.MessageDao
import com.example.zyndrav0.data.database.entities.Conversation
import com.example.zyndrav0.data.database.entities.MessageEntity
import com.example.zyndrav0.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {

    fun getAllConversations(userId: String): Flow<List<Conversation>> {
        return conversationDao.getAllConversations(userId)
    }

    fun getConversationById(conversationId: Long): Flow<Conversation?> {
        return conversationDao.getConversationById(conversationId)
    }

    fun getMessagesByConversation(conversationId: Long): Flow<List<Message>> {
        return messageDao.getMessagesByConversation(conversationId).map { entities ->
            entities.map { entity ->
                Message(
                    text = entity.text,
                    isUser = entity.isUser,
                    timestamp = entity.timestamp,
                    attachmentUri = entity.attachmentUri,
                    attachmentType = entity.attachmentType
                )
            }
        }
    }

    suspend fun createConversation(userId: String, title: String): Long {
        val conversation = Conversation(
            userId = userId,
            title = title
        )
        return conversationDao.insertConversation(conversation)
    }

    suspend fun insertMessage(
        conversationId: Long,
        text: String,
        isUser: Boolean,
        attachmentUri: String? = null,
        attachmentType: String? = null
    ): Long {
        val message = MessageEntity(
            conversationId = conversationId,
            text = text,
            isUser = isUser,
            attachmentUri = attachmentUri,
            attachmentType = attachmentType
        )
        val messageId = messageDao.insertMessage(message)

        // Actualizar última mensaje de la conversación
        conversationDao.updateLastMessage(
            conversationId = conversationId,
            message = text,
            timestamp = message.timestamp
        )

        return messageId
    }

    suspend fun deleteConversation(conversationId: Long) {
        messageDao.deleteMessagesByConversation(conversationId)
        conversationDao.deleteConversationById(conversationId)
    }

    suspend fun archiveConversation(conversationId: Long) {
        conversationDao.archiveConversation(conversationId)
    }

    suspend fun getMessageCount(conversationId: Long): Int {
        return messageDao.getMessageCount(conversationId)
    }
}
