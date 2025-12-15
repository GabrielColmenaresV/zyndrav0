package com.example.zyndrav0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.database.AppDatabase
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.GachaRepository
import com.example.zyndrav0.data.repository.UserRepository
import com.example.zyndrav0.model.GachaCatalog
import com.example.zyndrav0.model.GachaItem
import com.example.zyndrav0.model.Rarity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GachaViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val database = AppDatabase.getDatabase(application)

    private val userRepository = UserRepository(
        database.userDao(),
        database.userPreferencesDao()
    )

    private val gachaRepository = GachaRepository(
        database.gachaInventoryDao(),
        database.userDao()
    )

    private val _currency = MutableStateFlow(0)
    val currency: StateFlow<Int> = _currency.asStateFlow()

    private val _lastPullResult = MutableStateFlow<List<GachaItem>>(emptyList())
    val lastPullResult: StateFlow<List<GachaItem>> = _lastPullResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val gachaPool: List<GachaItem> = GachaCatalog.defaultPool()

    init {
        loadUserCurrency()
    }

    private fun loadUserCurrency() {
        viewModelScope.launch {
            val userId = sessionManager.userId.first() ?: return@launch
            userRepository.getUserById(userId).collect { user ->
                _currency.value = user?.currency ?: 0
            }
        }
    }

    fun pullGacha(count: Int) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first() ?: return@launch
            val pullCost = if (count == 10) 50 else 5

            if (_currency.value < pullCost) {
                _statusMessage.value = "No tienes suficientes monedas."
                return@launch
            }

            _isLoading.value = true
            _statusMessage.value = "Realizando tirada..."

            try {
                val results = mutableListOf<GachaItem>()
                val costPerPull = pullCost / count

                repeat(count) {
                    val pullResult = gachaRepository.pullGacha(userId, costPerPull)
                    val item = pullResult.first
                    if (item != null) {
                        results.add(item)
                    }
                }

                if (count == 10 && results.none { it.rarity >= Rarity.SR }) {
                    val guaranteedPullResult = gachaRepository.pullGacha(userId, 0)
                    val guaranteedItem = guaranteedPullResult.first
                    if (guaranteedItem != null && results.isNotEmpty()) {
                        results[results.indices.random()] = guaranteedItem
                    }
                }

                _lastPullResult.value = results
                _statusMessage.value = "¡Tirada completada! ${results.size} ítems guardados en inventario."

            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Error al realizar la tirada: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getItemsByIds(ids: List<Int>): List<GachaItem> {
        return ids.mapNotNull { id -> gachaPool.find { it.id == id } }
    }

    fun clearLastPull() {
        _lastPullResult.value = emptyList()
    }
}