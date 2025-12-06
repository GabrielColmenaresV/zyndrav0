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

class ProfileViewModel(
    application: Application,
    private val sessionManager: SessionManager = SessionManager(application),
    private val userRepository: UserRepository = UserRepository(
        AppDatabase.getDatabase(application).userDao(),
        AppDatabase.getDatabase(application).userPreferencesDao()
    ),
    private val gachaRepository: GachaRepository = GachaRepository(
        AppDatabase.getDatabase(application).gachaInventoryDao(),
        AppDatabase.getDatabase(application).userDao()
    )
) : AndroidViewModel(application) {

    // Ya no creamos nada aquí adentro, usamos las variables del constructor 👆

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _inventoryCount = MutableStateFlow(0)
    val inventoryCount: StateFlow<Int> = _inventoryCount

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = sessionManager.userId.first()
                val userName = sessionManager.userName.first()

                if (userId == null) {
                    _user.value = null
                    _isLoading.value = false
                    return@launch
                }

                val localUser = try {
                    userRepository.getUserById(userId).first()
                } catch (e: Exception) {
                    null
                }

                if (localUser != null) {
                    _user.value = localUser
                } else {
                    val displayUser = User(
                        userId = userId,
                        username = userName ?: "Usuario",
                        email = "", // dejenlo vacio o se bugea
                        passwordHash = "",
                        profileImageUri = null
                    )

                    saveUserLocally(displayUser)
                    _user.value = displayUser
                }

                // El
                val count = try {
                    gachaRepository.getInventoryCount(userId)
                } catch (e: Exception) {
                    0
                }
                _inventoryCount.value = count

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun saveUserLocally(user: User) {
        viewModelScope.launch {
            try {
                if (userRepository.getUserById(user.userId).first() == null) {
                    userRepository.createUser(
                        email = user.email,
                        username = user.username,
                        password = "api_user"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.userId.first() ?: return@launch
                userRepository.updateProfileImage(userId, uri.toString())

                val currentUser = _user.value
                if (currentUser != null) {
                    _user.value = currentUser.copy(profileImageUri = uri.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}