package com.example.zyndrav0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.database.AppDatabase
import com.example.zyndrav0.data.database.entities.Conversation
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatHistoryViewModel(
    application: Application,
    private val sessionManager: SessionManager = SessionManager(application),
    private val chatRepository: ChatRepository = ChatRepository(
        AppDatabase.getDatabase(application).conversationDao(),
        AppDatabase.getDatabase(application).messageDao()
    )
) : AndroidViewModel(application) {


    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = sessionManager.userId.first() ?: run {
                    _isLoading.value = false
                    return@launch
                }

                chatRepository.getAllConversations(userId).collect { conversations ->
                    _conversations.value = conversations
                    if (_isLoading.value) {
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
                _conversations.value = emptyList()
            }
        }
    }

    suspend fun createNewConversation(): Long {
        val userId = sessionManager.userId.first() ?: return -1
        val title = "Nueva conversación ${System.currentTimeMillis()}"
        return chatRepository.createConversation(userId, title)
    }

    fun deleteConversation(conversationId: Long) {
        viewModelScope.launch {
            try {
                chatRepository.deleteConversation(conversationId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun archiveConversation(conversationId: Long) {
        viewModelScope.launch {
            try {
                chatRepository.archiveConversation(conversationId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}