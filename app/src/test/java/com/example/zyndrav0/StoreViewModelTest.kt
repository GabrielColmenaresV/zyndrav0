package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.UserRepository
import com.example.zyndrav0.viewmodel.CoinBundle
import com.example.zyndrav0.viewmodel.StoreViewModel
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
class StoreViewModelTest {

    @Mock lateinit var userRepositoryFalso: UserRepository
    @Mock lateinit var sessionManagerFalso: SessionManager
    @Mock lateinit var applicationFalsa: Application

    private val testDispatcher = UnconfinedTestDispatcher()
    lateinit var viewModel: StoreViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Metemos los mockeos
        viewModel = StoreViewModel(applicationFalsa, sessionManagerFalso, userRepositoryFalso)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `si no hay usuario, muestra error`() = runTest {
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(null))

        val paquete = CoinBundle("Test", 100, "$1")

        // WHEN
        viewModel.purchaseCoins(paquete)

        // THEN
        assertEquals("No pudimos identificar al usuario.", viewModel.statusMessage.value)
    }

    @Test
    fun `compra exitosa muestra mensaje de exito`() = runTest {
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf("1"))

        val paquete = CoinBundle("Test", 50, "$1")

        // WHEN
        viewModel.purchaseCoins(paquete)

        // THEN
        val mensaje = viewModel.statusMessage.value
        assertTrue(mensaje?.contains("Compra simulada") == true)
        assertTrue(mensaje?.contains("+50") == true)

        // Verificamos que realmente llamó al repositorio para sumar monedas
        Mockito.verify(userRepositoryFalso).addCurrency("1", 50)
    }

    @Test
    fun `fallo en base de datos muestra error`() = runTest {
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf("1"))
        Mockito.`when`(userRepositoryFalso.addCurrency("1", 100))
            .thenThrow(RuntimeException("Error de base de datos"))

        val paquete = CoinBundle("Test", 100, "$1")

        // WHEN
        viewModel.purchaseCoins(paquete)

        // THEN
        val mensaje = viewModel.statusMessage.value
        assertTrue(mensaje?.contains("Error al procesar") == true)
    }
}