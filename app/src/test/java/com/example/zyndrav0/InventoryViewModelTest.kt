package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.database.entities.GachaInventoryItem
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.GachaRepository
import com.example.zyndrav0.viewmodel.InventoryViewModel
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
class InventoryViewModelTest {

    @Mock lateinit var gachaRepoFalso: GachaRepository
    @Mock lateinit var sessionManagerFalso: SessionManager
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
    fun `al iniciar carga el inventario del usuario`() = runTest {
        val userId = "10"

        val itemsFalsos = listOf(
            GachaInventoryItem(
                inventoryId = 1L,
                userId = userId,
                itemId = 100,
                itemName = "Espada",
                itemDescription = "Una espada de prueba",
                rarity = "common",
                itemType = "weapon",
                isEquipped = false
            ),
            GachaInventoryItem(
                inventoryId = 2L,
                userId = userId,
                itemId = 101,
                itemName = "Escudo",
                itemDescription = "Un escudo de prueba",
                rarity = "rare",
                itemType = "shield",
                isEquipped = false
            )
        )

        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))
        Mockito.`when`(gachaRepoFalso.getAllItems(userId)).thenReturn(flowOf(itemsFalsos))

        val viewModel = InventoryViewModel(applicationFalsa, sessionManagerFalso, gachaRepoFalso)

        assertEquals(2, viewModel.inventory.value.size)
        assertEquals("Espada", viewModel.inventory.value[0].itemName)
    }

    @Test
    fun `equipItem llama al repositorio correctamente`() = runTest {
        val userId = "50"
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))
        Mockito.`when`(gachaRepoFalso.getAllItems(userId)).thenReturn(flowOf(emptyList()))

        val viewModel = InventoryViewModel(applicationFalsa, sessionManagerFalso, gachaRepoFalso)

        viewModel.equipItem(99L, "weapon")

        Mockito.verify(gachaRepoFalso).equipItem(userId, 99L, "weapon")
    }
}