package com.example.zyndrav0.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.database.AppDatabase
import com.example.zyndrav0.data.database.entities.User
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.UserRepository
import com.example.zyndrav0.model.LoginRequest
import com.example.zyndrav0.network.AuthRetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = AuthRetrofitClient.api
    private val sessionManager = SessionManager(application)

    // Inicializamos la base de datos local
    private val database = AppDatabase.getDatabase(application)
    private val userRepository = UserRepository(
        database.userDao(),
        database.userPreferencesDao()
    )

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Por favor, ingresa correo y contraseña."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            try {
                val request = LoginRequest(email = email, password = password)
                val response = apiService.loginUser(request)

                if (response.isSuccessful && response.body() != null) {
                    val apiData = response.body()!!

                    if (apiData.user != null) {
                        val usuarioApi = apiData.user
                        val userIdStr = usuarioApi.id.toString()

                        // 1. Guardar sesión
                        sessionManager.saveSession(
                            userId = userIdStr,
                            email = usuarioApi.email,
                            userName = usuarioApi.username
                        )

                        val userLocal = User(
                            userId = userIdStr,
                            username = usuarioApi.username,
                            email = usuarioApi.email,
                            passwordHash = ""
                        )

                        try {
                            userRepository.insertUserFromApi(userLocal)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        onSuccess()
                    } else {
                        errorMessage = "Error: El servidor no devolvió los datos del usuario."
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = "Credenciales incorrectas."
                }

            } catch (e: Exception) {
                errorMessage = "Error de conexión: Verifica tu internet o el servidor."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}