package com.example.zyndrav0.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.model.RegisterRequest
import com.example.zyndrav0.network.AuthApiService
import com.example.zyndrav0.network.AuthRetrofitClient
import kotlinx.coroutines.launch

class RegistroViewModel(
    application: Application,
    private val apiService: AuthApiService = AuthRetrofitClient.api,
    private val sessionManager: SessionManager = SessionManager(application)
) : AndroidViewModel(application) {


    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun register(onSuccess: () -> Unit) {
        if (!isValidEmail(email)) {
            errorMessage = "Por favor, introduce un correo electrónico válido."
            return
        }
        if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres."
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                )


                val response = apiService.registerUser(request)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    val userId = apiResponse.user?.id?.toString() ?: "0"
                    val userName = apiResponse.user?.username ?: username

                    sessionManager.saveSession(
                        userId = userId,
                        email = email,
                        userName = userName
                    )

                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = "Error: ${response.code()} - $errorBody"
                }

            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}