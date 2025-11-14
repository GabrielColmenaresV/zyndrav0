package com.example.zyndrav0.model

import androidx.compose.ui.graphics.Color
import com.example.zyndrav0.ui.theme.RedCore

// Define la rareza de un ítem del Gacha, con una etiqueta y un color asociado
enum class Rarity(val label: String, val color: Color) {
    N("Normal", Color.Gray),
    R("Raro", Color.Cyan),
    SR("Súper Raro", Color(0xFFE57373)),
    SSR("Súper Súper Raro", Color(0xFFFFC107)),
    LR("Legendario Raro", RedCore)
}

// Representa un unico ítem que se puede obtener del Gacha
data class GachaItem(
    val id: Int,
    val name: String,
    val description: String,
    val rarity: Rarity,
    val itemType: GachaItemType,
    val imageUrl: String? = null, // Para mostrar una imag buscar
    val iconEmoji: String? = null, // Representacion visual buscar
    val colorHex: String? = null, // Color para aplicar a la burbuja/tema
    val fontFamily: String? = null
)

enum class GachaItemType {
    CHAT_BUBBLE,
    THEME,
    AVATAR_FRAME,
    STORY_KEY
}
