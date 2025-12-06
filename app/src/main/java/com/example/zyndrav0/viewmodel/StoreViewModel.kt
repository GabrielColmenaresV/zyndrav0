package com.example.zyndrav0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.database.AppDatabase
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CoinBundle(
    val title: String,
    val amount: Int,
    val priceLabel: String
)


class StoreViewModel(
    application: Application,
    private val sessionManager: SessionManager = SessionManager(application),
    private val userRepository: UserRepository = UserRepository(
        AppDatabase.getDatabase(application).userDao(),
        AppDatabase.getDatabase(application).userPreferencesDao()
    )
) : AndroidViewModel(application) {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    val bundles = listOf(
        CoinBundle("Recarga Pequeña", 50, "$0.99"),
        CoinBundle("Recarga Mediana", 120, "$1.99"),
        CoinBundle("Recarga Grande", 320, "$4.99")
    )

    fun consumeStatus() {
        _statusMessage.value = null
    }

    fun purchaseCoins(bundle: CoinBundle) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()

            if (userId == null) {
                _statusMessage.value = "No pudimos identificar al usuario."
                return@launch
            }

            _isProcessing.value = true
            _statusMessage.value = null

            try {
                userRepository.addCurrency(userId, bundle.amount)

                _statusMessage.value = "Compra simulada: +${bundle.amount} monedas añadidas."
            } catch (e: Exception) {
                _statusMessage.value = "Error al procesar la compra: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }
}