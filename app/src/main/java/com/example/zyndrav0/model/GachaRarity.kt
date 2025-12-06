package com.example.zyndrav0.model

import androidx.compose.ui.graphics.Color

enum class GachaRarity(val label: String, val color: Color, val probability: Double) {
    COMMON("Común", Color(0xFFBDBDBD), 0.60), // 60%
    RARE("Raro", Color(0xFF4FC3F7), 0.30),     // 30%
    SUPER_RARE("Súper Raro", Color(0xFFAB47BC), 0.09), // 9%
    LEGENDARY("Legendario", Color(0xFFFFD700), 0.01) // 1%
}
