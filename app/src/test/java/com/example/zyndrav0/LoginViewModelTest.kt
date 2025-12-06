package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.model.LoginRequest
import com.example.zyndrav0.model.LoginResponse
import com.example.zyndrav0.model.UserDto
import com.example.zyndrav0.network.AuthApiService
import com.example.zyndrav0.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    // Todos los mocks
    @Mock
    lateinit var apiFalsa: AuthApiService
    @Mock
    lateinit var sessionManagerFalso: SessionManager
    @Mock
    lateinit var applicationFalsa: Application

    // El Dispatcher para pruebas simulando el hilo central (principal)
    private val testDispatcher = UnconfinedTestDispatcher()

    // El ViewModel real
    lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher) // el falso

        viewModel = LoginViewModel(applicationFalsa, apiFalsa, sessionManagerFalso)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `si los campos estan vacios, debe marcar error`() {
        // GIVEN (Dado que...)
        viewModel.email = ""
        viewModel.password = ""

        // WHEN (Cuando intentamos login...)
        viewModel.login {}

        // THEN (Entonces...)
        assertEquals("Por favor, ingresa correo y contraseña.", viewModel.errorMessage)
    }

    @Test
    fun `si la API responde exito, debe llamar a onSuccess`() = runTest {
        // GIVEN
        viewModel.email = "test@prueba.com"
        viewModel.password = "123456"

        val usuarioFake = UserDto(1, "Test User", "test@prueba.com")
        val responseFake = LoginResponse("token123", usuarioFake, null)

        Mockito.`when`(apiFalsa.loginUser(LoginRequest("test@prueba.com", "123456")))
            .thenReturn(Response.success(responseFake))

        var loginExitoso = false

        // WHEN
        viewModel.login(onSuccess = {
            loginExitoso = true
        })

        // THEN
        assertTrue("El login debería ser exitoso", loginExitoso)
        assertTrue(viewModel.errorMessage.isEmpty())
    }

    @Test
    fun `si la API falla (401), debe mostrar mensaje de error`() = runTest {
        // GIVEN
        viewModel.email = "test@prueba.com"
        viewModel.password = "malapassword"

        // error 401
        val errorResponse = Response.error<LoginResponse>(401, okhttp3.ResponseBody.create(null, "Error"))

        Mockito.`when`(apiFalsa.loginUser(LoginRequest("test@prueba.com", "malapassword")))
            .thenReturn(errorResponse)

        // WHEN
        viewModel.login {}

        // THEN
        assertEquals("Credenciales incorrectas.", viewModel.errorMessage)
    }
}