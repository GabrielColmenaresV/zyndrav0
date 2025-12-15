package com.example.zyndrav0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.datastore.SessionManager
import com.example.zyndrav0.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CoinPackage(
    val id: String,
    val coinsAmount: Int,
    val priceCLP: Int,
    val description: String
)

data class SavedCard(
    val holderName: String = "",
    val number: String = "",
    val expiry: String = "",
    val cvv: String = ""
)

class StoreViewModel(
    application: Application,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    val products = listOf(
        CoinPackage("p1", 100, 1000, "Puñado de Monedas"),
        CoinPackage("p2", 550, 5000, "Bolsa de Monedas (+50 gratis)"),
        CoinPackage("p3", 1200, 10000, "Cofre de Monedas (+200 gratis)"),
        CoinPackage("p4", 3000, 25000, "Tesoro Legendario")
    )

    private val _userCoins = MutableStateFlow(0)
    val userCoins: StateFlow<Int> = _userCoins

    private val _savedCard = MutableStateFlow(SavedCard())
    val savedCard: StateFlow<SavedCard> = _savedCard

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _purchaseStatus = MutableStateFlow<String?>(null)
    val purchaseStatus: StateFlow<String?> = _purchaseStatus

    init {
        observeUserCurrency()
    }

    private fun observeUserCurrency() {
        viewModelScope.launch {
            sessionManager.userId.collect { userId ->
                if (userId != null) {
                    userRepository.getUserById(userId).collect { user ->
                        if (user != null) {
                            _userCoins.value = user.currency
                        }
                    }
                }
            }
        }
    }

    fun processPayment(card: SavedCard, product: CoinPackage) {
        viewModelScope.launch {
            _isProcessing.value = true
            _purchaseStatus.value = null

            if (card.holderName.isBlank()) {
                _purchaseStatus.value = "Error: Falta el nombre del titular."
                _isProcessing.value = false
                return@launch
            }

            val cleanNumber = card.number.filter { it.isDigit() }
            if (cleanNumber.length != 16) {
                _purchaseStatus.value = "Error: La tarjeta debe tener 16 números."
                _isProcessing.value = false
                return@launch
            }

            if (!card.expiry.contains("/") || card.expiry.length != 5) {
                _purchaseStatus.value = "Error: Fecha inválida (Use formato MM/AA)."
                _isProcessing.value = false
                return@launch
            }

            val cleanCVV = card.cvv.filter { it.isDigit() }
            if (cleanCVV.length != 3) {
                _purchaseStatus.value = "Error: El CVV debe tener 3 dígitos."
                _isProcessing.value = false
                return@launch
            }
            _savedCard.value = card

            // Simulamos espera de red
            delay(1500)

            try {
                val userId = sessionManager.userId.first()
                if (userId != null) {
                    // Usamos la función que YA TIENES en tu UserRepository
                    userRepository.addCurrency(userId, product.coinsAmount)
                    _purchaseStatus.value = "EXITO: ¡Compra realizada correctamente!"
                } else {
                    _purchaseStatus.value = "Error: No se encontró sesión de usuario."
                }
            } catch (e: Exception) {
                _purchaseStatus.value = "Error en base de datos: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun clearStatus() {
        _purchaseStatus.value = null
    }
}