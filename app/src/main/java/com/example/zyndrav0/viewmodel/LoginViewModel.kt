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

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val sessionManager = SessionManager(application)
    private val userRepository = UserRepository(
        database.userDao(),
        database.userPreferencesDao()
    )

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun login(onSuccess: () -> Unit) {
        if (!isValidEmail(email)) {
            errorMessage = "Por favor, introduce un correo electrónico válido."
            return
        }

        if (password.isBlank()) {
            errorMessage = "La contraseña no puede estar vacía."
            return
        }

        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = ""

                val user = userRepository.getUserByEmail(email)

                if (user == null) {
                    errorMessage = "El usuario no existe. Por favor, regístrate."
                    return@launch
                }

                if (user.passwordHash != hashPassword(password)) {
                    errorMessage = "La contraseña es incorrecta."
                    return@launch
                }

                userRepository.updateLastLogin(user.userId)

                sessionManager.saveSession(
                    userId = user.userId,
                    email = user.email,
                    userName = user.username
                )

                onSuccess()

            } catch (e: Exception) {
                errorMessage = "Error al iniciar sesión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }
}