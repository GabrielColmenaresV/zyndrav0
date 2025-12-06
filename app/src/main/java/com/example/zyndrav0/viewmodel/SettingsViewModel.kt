package com.example.zyndrav0.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val userBubbleColor = mutableStateOf(Color(0xFF6200EE))
    val assistantBubbleColor = mutableStateOf(Color(0xFFE0E0E0))

    val isDarkTheme = mutableStateOf(false)

    fun updateUserBubbleColor(color: Color) {
        userBubbleColor.value = color
    }

    fun updateAssistantBubbleColor(color: Color) {
        assistantBubbleColor.value = color
    }

    fun toggleDarkTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }
}
