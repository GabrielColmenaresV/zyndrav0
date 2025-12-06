package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.ChatRepository
import com.example.zyndrav0.data.repository.GachaRepository
import com.example.zyndrav0.data.repository.UserRepository
import com.example.zyndrav0.network.ChatMessage
import com.example.zyndrav0.network.N8nApiService
import com.example.zyndrav0.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @Mock lateinit var sessionManagerFalso: SessionManager
    @Mock lateinit var chatRepoFalso: ChatRepository
    @Mock lateinit var userRepoFalso: UserRepository
    @Mock lateinit var gachaRepoFalso: GachaRepository
    @Mock lateinit var apiFalsa: N8nApiService
    @Mock lateinit var applicationFalsa: Application

    private val testDispatcher = UnconfinedTestDispatcher()

    private val dummyMessage = ChatMessage("", "", 0L)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al enviar mensaje en chat nuevo, crea conversacion y suma monedas`() = runTest {
        val userId = "99"
        val nuevoChatId = 500L

        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))
        Mockito.`when`(gachaRepoFalso.getEquippedItems(userId)).thenReturn(flowOf(emptyList()))
        Mockito.`when`(chatRepoFalso.createConversation(userId, "Nuevo Chat")).thenReturn(nuevoChatId)
        Mockito.`when`(chatRepoFalso.getMessagesByConversation(Mockito.anyLong())).thenReturn(flowOf(emptyList()))

        // Mock API
        Mockito.`when`(apiFalsa.sendMessage(Mockito.any(ChatMessage::class.java) ?: dummyMessage))
            .thenReturn("Soy el Bot")

        val viewModel = ChatViewModel(
            applicationFalsa,
            -1L,
            sessionManagerFalso, chatRepoFalso, userRepoFalso, gachaRepoFalso, apiFalsa
        )

        viewModel.sendMessage("Hola")

        Mockito.verify(chatRepoFalso).createConversation(userId, "Nuevo Chat")
        Mockito.verify(userRepoFalso).addCurrency(userId, 100)
        assertEquals(nuevoChatId, viewModel.currentConversationId)

        Mockito.verify(chatRepoFalso).insertMessage(
            conversationId = nuevoChatId,
            text = "Soy el Bot",
            isUser = false,
            attachmentUri = null,
            attachmentType = null
        )
    }

    @Test
    fun `si la API falla, guarda mensaje de error`() = runTest {
        val chatId = 10L
        val userId = "99"

        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))
        Mockito.`when`(gachaRepoFalso.getEquippedItems(userId)).thenReturn(flowOf(emptyList()))
        Mockito.`when`(chatRepoFalso.getMessagesByConversation(chatId)).thenReturn(flowOf(emptyList()))

        Mockito.`when`(apiFalsa.sendMessage(Mockito.any(ChatMessage::class.java) ?: dummyMessage))
            .thenThrow(RuntimeException("Error Fatal"))

        val viewModel = ChatViewModel(
            applicationFalsa,
            chatId,
            sessionManagerFalso, chatRepoFalso, userRepoFalso, gachaRepoFalso, apiFalsa
        )

        viewModel.sendMessage("Hola")

        Mockito.verify(chatRepoFalso).insertMessage(
            conversationId = Mockito.eq(chatId) ?: 0L,
            text = Mockito.contains("Error de red"),
            isUser = Mockito.eq(false) ?: false,
            attachmentUri = Mockito.isNull(),
            attachmentType = Mockito.isNull()
        )

        assertEquals("Error", viewModel.statusText)
    }
}