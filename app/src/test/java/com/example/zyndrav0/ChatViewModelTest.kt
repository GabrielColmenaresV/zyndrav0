package com.example.zyndrav0.viewmodel

import androidx.test.core.app.ApplicationProvider
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.ChatRepository
import com.example.zyndrav0.data.repository.GachaRepository
import com.example.zyndrav0.data.repository.UserRepository
import com.example.zyndrav0.network.N8nApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking // IMPORTANTE
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ChatViewModelTest {

    @Mock lateinit var apiFalsa: N8nApiService
    @Mock lateinit var sessionManagerFalso: SessionManager
    @Mock lateinit var chatRepository: ChatRepository
    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var gachaRepository: GachaRepository

    private lateinit var viewModel: ChatViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        val application = ApplicationProvider.getApplicationContext<android.app.Application>()

        `when`(sessionManagerFalso.userId).thenReturn(flowOf("99"))
        `when`(chatRepository.getMessagesByConversation(anyLong())).thenReturn(flowOf(emptyList()))

        `when`(gachaRepository.getEquippedItems(anyString())).thenReturn(flowOf(emptyList()))

        runBlocking {
            doReturn(1L).`when`(chatRepository).createConversation(anyString(), anyString())

            doReturn(1L).`when`(chatRepository).insertMessage(anyLong(), anyString(), anyBoolean(), any(), any())
        }

        viewModel = ChatViewModel(
            application,
            -1L,
            sessionManagerFalso,
            chatRepository,
            userRepository,
            gachaRepository,
            apiFalsa
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando envio mensaje, el viewModel debe llamar al repositorio para guardar`() = runTest {
        val mensajeTexto = "Hola Test"
        val respuestaBot = "Respuesta simulada"

        // API Mock
        `when`(apiFalsa.sendMessage(any())).thenReturn(respuestaBot)

        viewModel.sendMessage(mensajeTexto)

        testScheduler.advanceUntilIdle()

        // Verificamos mensaje usuario
        verify(chatRepository, atLeastOnce()).insertMessage(
            anyLong(),
            anyString(),
            eq(true),
            any(),
            any()
        )

        // Verificamos mensaje bot
        verify(chatRepository, atLeastOnce()).insertMessage(
            anyLong(),
            anyString(),
            eq(false),
            any(),
            any()
        )

        verify(apiFalsa).sendMessage(any())
    }

    private fun <T> eq(value: T): T = Mockito.eq(value) ?: value

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}