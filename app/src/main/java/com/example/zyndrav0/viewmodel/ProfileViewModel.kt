package com.example.zyndrav0.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.database.AppDatabase
import com.example.zyndrav0.data.database.entities.User
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.GachaRepository
import com.example.zyndrav0.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val sessionManager = SessionManager(application)
    private val userRepository = UserRepository(
        database.userDao(),
        database.userPreferencesDao()
    )
    private val gachaRepository = GachaRepository(
        database.gachaInventoryDao(),
        database.userDao()
    )

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _inventoryCount = MutableStateFlow(0)
    val inventoryCount: StateFlow<Int> = _inventoryCount

    private val _isLoading = MutableStateFlow(true) // Start as true
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadInitialData()
        observeUserChanges()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = try {
                    sessionManager.userId.first()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                
                if (userId == null) {
                    _isLoading.value = false
                    return@launch
                }
                
                // Carga inicial del usuario
                val user = try {
                    userRepository.getUserById(userId).first()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                _user.value = user
                
                // Carga del conteo de inventario
                val count = try {
                    gachaRepository.getInventoryCount(userId)
                } catch (e: Exception) {
                    e.printStackTrace()
                    0
                }
                _inventoryCount.value = count

            } catch (e: Exception) {
                e.printStackTrace()
                // Asegurar que siempre se quite el loading incluso si hay error
                _user.value = null
                _inventoryCount.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun observeUserChanges() {
        viewModelScope.launch {
            try {
                val userId = sessionManager.userId.first() ?: return@launch
                userRepository.getUserById(userId).collect { user ->
                    _user.value = user
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Si hay error, no hacer nada, el usuario ya tiene un valor por defecto
            }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.userId.first() ?: return@launch
                userRepository.updateProfileImage(userId, uri.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
