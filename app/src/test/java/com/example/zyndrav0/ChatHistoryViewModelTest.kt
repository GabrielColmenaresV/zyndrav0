package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.database.entities.Conversation
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.ChatRepository
import com.example.zyndrav0.viewmodel.ChatHistoryViewModel
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
class ChatHistoryViewModelTest {

    @Mock lateinit var sessionManagerFalso: SessionManager
    @Mock lateinit var chatRepoFalso: ChatRepository
    @Mock lateinit var applicationFalsa: Application

    private val testDispatcher = UnconfinedTestDispatcher()

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
    fun `al iniciar carga las conversaciones del usuario`() = runTest {
        val userId = "User1"

        val listaFalsa = listOf(
            Conversation(
                conversationId = 1L,
                userId = userId,
                title = "Chat 1",
                isArchived = false
            ),
            Conversation(
                conversationId = 2L,
                userId = userId,
                title = "Chat 2",
                isArchived = false
            )
        )

        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))
        Mockito.`when`(chatRepoFalso.getAllConversations(userId)).thenReturn(flowOf(listaFalsa))

        val viewModel = ChatHistoryViewModel(applicationFalsa, sessionManagerFalso, chatRepoFalso)

        assertEquals(2, viewModel.conversations.value.size)
    }

    @Test
    fun `createNewConversation crea chat y devuelve ID`() = runTest {
        val userId = "User1"
        val nuevoId = 100L

        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))

        Mockito.`when`(chatRepoFalso.createConversation(
            Mockito.anyString() ?: "",
            Mockito.anyString() ?: ""
        )).thenReturn(nuevoId)

        // Mock necesario para el init
        Mockito.`when`(chatRepoFalso.getAllConversations(userId)).thenReturn(flowOf(emptyList()))

        val viewModel = ChatHistoryViewModel(applicationFalsa, sessionManagerFalso, chatRepoFalso)

        val idRetornado = viewModel.createNewConversation()

        assertEquals(nuevoId, idRetornado)

        // Verificación con matchers seguros
        Mockito.verify(chatRepoFalso).createConversation(
            Mockito.eq(userId) ?: "",
            Mockito.contains("Nueva conversación") ?: ""
        )
    }

    @Test
    fun `deleteConversation llama al repositorio`() = runTest {
        val userId = "User1"
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))
        Mockito.`when`(chatRepoFalso.getAllConversations(userId)).thenReturn(flowOf(emptyList()))

        val viewModel = ChatHistoryViewModel(applicationFalsa, sessionManagerFalso, chatRepoFalso)

        viewModel.deleteConversation(55L)

        Mockito.verify(chatRepoFalso).deleteConversation(55L)
    }
}