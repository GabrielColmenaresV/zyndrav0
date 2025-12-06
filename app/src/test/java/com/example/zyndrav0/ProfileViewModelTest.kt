package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.database.entities.User
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.GachaRepository
import com.example.zyndrav0.data.repository.UserRepository
import com.example.zyndrav0.viewmodel.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @Mock lateinit var userRepoFalso: UserRepository
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
    fun `si usuario existe en local, carga esos datos`() = runTest {
        val userId = "100"

        val usuarioLocal = User(
            userId = userId,
            username = "LocalUser",
            email = "local@test.com",
            passwordHash = "",
            profileImageUri = null
        )

        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))
        Mockito.`when`(sessionManagerFalso.userName).thenReturn(flowOf("LocalUser"))
        Mockito.`when`(userRepoFalso.getUserById(userId)).thenReturn(flowOf(usuarioLocal))
        Mockito.`when`(gachaRepoFalso.getInventoryCount(userId)).thenReturn(5)

        val viewModel = ProfileViewModel(applicationFalsa, sessionManagerFalso, userRepoFalso, gachaRepoFalso)

        // THEN
        assertEquals("LocalUser", viewModel.user.value?.username)
        assertEquals(5, viewModel.inventoryCount.value)
    }

    @Test
    fun `si usuario es nuevo de API, crea usuario temporal`() = runTest {
        val userId = "200"
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(userId))
        Mockito.`when`(sessionManagerFalso.userName).thenReturn(flowOf("ApiUser"))

        Mockito.`when`(userRepoFalso.getUserById(userId)).thenReturn(flowOf(null))
        Mockito.`when`(gachaRepoFalso.getInventoryCount(userId)).thenReturn(0)

        // WHEN
        val viewModel = ProfileViewModel(applicationFalsa, sessionManagerFalso, userRepoFalso, gachaRepoFalso)

        // THEN
        assertNotNull(viewModel.user.value)
        assertEquals("ApiUser", viewModel.user.value?.username)

        Mockito.verify(userRepoFalso).createUser("", "ApiUser", "api_user")
    }

    @Test
    fun `logout limpia la sesion`() = runTest {
        Mockito.`when`(sessionManagerFalso.userId).thenReturn(flowOf(null))
        Mockito.`when`(sessionManagerFalso.userName).thenReturn(flowOf(null))

        val viewModel = ProfileViewModel(applicationFalsa, sessionManagerFalso, userRepoFalso, gachaRepoFalso)

        // WHEN
        viewModel.logout()

        // THEN
        Mockito.verify(sessionManagerFalso).clearSession()
    }
}