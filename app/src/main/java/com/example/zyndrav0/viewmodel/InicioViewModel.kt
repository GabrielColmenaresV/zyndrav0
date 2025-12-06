package com.example.zyndrav0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.datastore.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InicioViewModel(
    application: Application,
    private val sessionManager: SessionManager = SessionManager(application)
) : AndroidViewModel(application) {

    val isLoggedIn: Flow<Boolean> = sessionManager.isLoggedIn

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}