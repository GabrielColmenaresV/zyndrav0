package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.database.entities.User
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.GachaRepository
import com.example.zyndrav0.data.repository.UserRepository
import com.example.zyndrav0.model.GachaItem
import com.example.zyndrav0.model.GachaItemType
import com.example.zyndrav0.model.Rarity
import com.example.zyndrav0.viewmodel.GachaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class GachaViewModelTest {

    @Mock lateinit var sessionManagerFalso: SessionManager
    @Mock lateinit var userRepoFalso: UserRepository
    @Mock lateinit var gachaRepoFalso: GachaRepository
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
    fun `si no tiene dinero suficiente, muestra error`() = runTest {
        // pato
        val userId = "10"
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))

        // Creamos un usuario pobre
        val usuarioPobre = User(
            userId = userId,
            username = "Pobre",
            email = "pobre@test.com",
            passwordHash = "",
            profileImageUri = null
        ).copy(currency = 0)

        Mockito.`when`(userRepoFalso.getUserById(userId)).thenReturn(flowOf(usuarioPobre))

        // WHEN: Intenta tirar el gacha y no tiene un peso
        val viewModel = GachaViewModel(applicationFalsa, sessionManagerFalso, userRepoFalso, gachaRepoFalso)
        viewModel.pullGacha(1)

        // THEN: Error
        assertEquals("No tienes suficientes monedas.", viewModel.statusMessage.value)
    }

    @Test
    fun `si tiene dinero, realiza la tirada exitosamente`() = runTest {
        val userId = "20"
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))

        val usuarioRico = User(
            userId = userId,
            username = "Rico",
            email = "rico@test.com",
            passwordHash = "",
            profileImageUri = null
        ).copy(currency = 1000)

        Mockito.`when`(userRepoFalso.getUserById(userId)).thenReturn(flowOf(usuarioRico))

        val itemFalso = GachaItem(
            id = 99,
            name = "Burbuja de Test",
            description = "Una burbuja para testing",
            rarity = Rarity.SSR,
            itemType = GachaItemType.THEME,
            imageUrl = null,
            iconEmoji = "🧪",
            colorHex = "#FFFFFF",
            fontFamily = "sans"
        )


        Mockito.`when`(gachaRepoFalso.pullGacha(Mockito.anyString(), Mockito.anyInt()))
            .thenReturn(Pair(itemFalso, "true"))

        // Tiramos el Gacha
        val viewModel = GachaViewModel(applicationFalsa, sessionManagerFalso, userRepoFalso, gachaRepoFalso)
        viewModel.pullGacha(1)

        // THEN
        val resultados = viewModel.lastPullResult.value
        assertEquals(1, resultados.size)
        assertEquals("Burbuja de Test", resultados[0].name)
        assertTrue(viewModel.statusMessage.value.contains("Tirada completada"))
    }
}