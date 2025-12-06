package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.viewmodel.InicioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModelTest {

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
    fun `isLoggedIn refleja el estado del sessionManager`() = runTest {
        // true
        Mockito.`when`(sessionManagerFalso.isLoggedIn).thenReturn(flowOf(true))

        // when
        val viewModel = InicioViewModel(applicationFalsa, sessionManagerFalso)

        // then
        val estadoLogin = viewModel.isLoggedIn.first()
        assertTrue("Debería estar logueado", estadoLogin)
    }

    @Test
    fun `logout llama a limpiar sesion`() = runTest {
        Mockito.`when`(sessionManagerFalso.isLoggedIn).thenReturn(flowOf(true))

        val viewModel = InicioViewModel(applicationFalsa, sessionManagerFalso)
        viewModel.logout()


        Mockito.verify(sessionManagerFalso).clearSession()
    }
}