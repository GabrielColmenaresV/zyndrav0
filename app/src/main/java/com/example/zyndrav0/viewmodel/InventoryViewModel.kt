package com.example.zyndrav0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.database.AppDatabase
import com.example.zyndrav0.data.database.entities.GachaInventoryItem
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.GachaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class InventoryViewModel(
    application: Application,
    private val sessionManager: SessionManager = SessionManager(application),
    private val gachaRepository: GachaRepository = GachaRepository(
        AppDatabase.getDatabase(application).gachaInventoryDao(),
        AppDatabase.getDatabase(application).userDao()
    )
) : AndroidViewModel(application) {


    private val _inventory = MutableStateFlow<List<GachaInventoryItem>>(emptyList())
    val inventory: StateFlow<List<GachaInventoryItem>> = _inventory

    init {
        loadInventory()
    }

    private fun loadInventory() {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null) {
                // Asumiendo que tu repo devuelve un Flow, usamos collect
                gachaRepository.getAllItems(userId).collect { items ->
                    _inventory.value = items
                }
            }
        }
    }

    fun equipItem(inventoryId: Long, itemType: String) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null) {
                gachaRepository.equipItem(userId, inventoryId, itemType)
            }
        }
    }
}