package com.example.zyndrav0.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.database.AppDatabase
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.ChatRepository
import com.example.zyndrav0.data.repository.GachaRepository
import com.example.zyndrav0.data.repository.UserRepository
import com.example.zyndrav0.model.GachaItemType
import com.example.zyndrav0.model.Message
import com.example.zyndrav0.network.ChatMessage
import com.example.zyndrav0.network.N8nApiService // Asegura este import (tu interfaz de Retrofit)
import com.example.zyndrav0.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application,
    val conversationId: Long,
    private val sessionManager: SessionManager,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val gachaRepository: GachaRepository,
    private val apiService: N8nApiService // La Maldita API
) : AndroidViewModel(application) {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _equippedBubbleId = MutableStateFlow<Int?>(null)
    val equippedBubbleId: StateFlow<Int?> = _equippedBubbleId

    private val _equippedThemeColor = MutableStateFlow<String?>(null)
    val equippedThemeColor: StateFlow<String?> = _equippedThemeColor

    private val _equippedFontFamily = MutableStateFlow<String?>(null)
    val equippedFontFamily: StateFlow<String?> = _equippedFontFamily

    // para testing
    var currentConversationId = conversationId
        private set

    var statusText by mutableStateOf("Estado: Listo para enviar.")
        private set

    init {
        if (currentConversationId != -1L) {
            loadMessages()
        }
        observeEquippedItems()
    }

    private fun observeEquippedItems() {
        viewModelScope.launch {
            val userId = sessionManager.userId.first() ?: return@launch
            gachaRepository.getEquippedItems(userId).collect { items ->
                val bubbleItem = items.firstOrNull { it.itemType == GachaItemType.CHAT_BUBBLE.name }
                _equippedBubbleId.value = bubbleItem?.itemId
                val bubbleColor = bubbleItem?.colorHex
                val bubbleFont = bubbleItem?.fontFamily

                val themeItem = items.firstOrNull { it.itemType == GachaItemType.THEME.name }
                val themeColor = themeItem?.colorHex?.takeIf { it.isNotBlank() }
                val themeFont = themeItem?.fontFamily?.takeIf { it.isNotBlank() }

                _equippedThemeColor.value = themeColor ?: bubbleColor
                _equippedFontFamily.value = themeFont ?: bubbleFont
            }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.getMessagesByConversation(currentConversationId).collect { _messages.value = it }
        }
    }

    fun sendMessage(messageText: String, attachmentUri: Uri? = null, attachmentType: String? = null) {
        if (messageText.isBlank() && attachmentUri == null) return

        viewModelScope.launch {
            val userId = sessionManager.userId.first() ?: return@launch

            if (currentConversationId == -1L) {
                currentConversationId = chatRepository.createConversation(userId, "Nuevo Chat")
                userRepository.addCurrency(userId, 100)
                chatRepository.insertMessage(
                    conversationId = currentConversationId,
                    text = "¡Has iniciado una nueva conversación! Por enviar tu primer mensaje, has ganado 100 monedas.",
                    isUser = false
                )
                loadMessages()
            }

            chatRepository.insertMessage(
                conversationId = currentConversationId,
                text = messageText,
                isUser = true,
                attachmentUri = attachmentUri?.toString(),
                attachmentType = attachmentType
            )

            try {
                statusText = "Enviando..."

                val apiResponse = apiService.sendMessage(
                    ChatMessage(
                        sender = "user",
                        message = messageText,
                        timestamp = System.currentTimeMillis()
                    )
                )

                chatRepository.insertMessage(
                    conversationId = currentConversationId,
                    text = apiResponse,
                    isUser = false
                )
                statusText = "Listo"

            } catch (e: Exception) {
                val errorMessage = "Error de red: ${e.message}"
                chatRepository.insertMessage(
                    conversationId = currentConversationId,
                    text = errorMessage,
                    isUser = false
                )
                statusText = "Error"
            }
        }
    }

    class Factory(private val application: Application, private val conversationId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                val db = AppDatabase.getDatabase(application)
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(
                    application,
                    conversationId,
                    SessionManager(application),
                    ChatRepository(db.conversationDao(), db.messageDao()),
                    UserRepository(db.userDao(), db.userPreferencesDao()),
                    GachaRepository(db.gachaInventoryDao(), db.userDao()),
                    RetrofitClient.instance
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}