package com.example.zyndrav0.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.database.AppDatabase
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.security.MessageDigest

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val userRepository = UserRepository(
        AppDatabase.getDatabase(application).userDao(),
        AppDatabase.getDatabase(application).userPreferencesDao()
    )

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun register(onSuccess: () -> Unit) {
        if (!isValidEmail(email)) {
            errorMessage = "Por favor, introduce un correo electrónico válido (gmail.com)."
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
                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    errorMessage = "El correo electrónico ya está en uso."
                    return@launch
                }

                val user = userRepository.createUser(
                    email = email,
                    username = username,
                    password = password
                )
                // Guardar session localmente para quedar autenticado tras registrarse
                //Asi evitamos el teener que ingresar las credenciales cada vez
                sessionManager.saveSession(
                    userId = user.userId,
                    email = user.email,
                    userName = user.username
                )
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Error al registrar: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("gmail.com")
    }
}