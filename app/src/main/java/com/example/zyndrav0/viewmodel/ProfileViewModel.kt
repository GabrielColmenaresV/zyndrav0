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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val database = AppDatabase.getDatabase(application)

    private val userRepository = UserRepository(database.userDao(), database.userPreferencesDao())
    private val gachaRepository = GachaRepository(database.gachaInventoryDao(), database.userDao())

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _inventoryCount = MutableStateFlow(0)
    val inventoryCount: StateFlow<Int> = _inventoryCount

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = sessionManager.userId.first()

            if (userId == null) {
                _isLoading.value = false
                return@launch
            }

            launch {
                userRepository.getUserById(userId).collectLatest { userActualizado ->
                    if (userActualizado != null) {
                        _user.value = userActualizado
                    } else {
                        val nuevoUser = User(
                            userId = userId,
                            username = sessionManager.userName.first() ?: "Usuario",
                            email = "",
                            passwordHash = ""
                        )
                        saveUserLocally(nuevoUser)
                    }
                }
            }

            launch {
                try {
                    _inventoryCount.value = gachaRepository.getInventoryCount(userId)
                } catch (e: Exception) {
                    _inventoryCount.value = 0
                }
            }

            _isLoading.value = false
        }
    }

    private fun saveUserLocally(user: User) {
        viewModelScope.launch {
            try {
                userRepository.createUser(user.email, user.username, "local_pass")
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first() ?: return@launch
            userRepository.updateProfileImage(userId, uri.toString())
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}