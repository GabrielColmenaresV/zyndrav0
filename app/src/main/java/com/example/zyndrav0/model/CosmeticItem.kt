package com.example.zyndrav0.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// Tipo de cosmetico
enum class CosmeticType {
    BUBBLE,
    ICON,
    BACKGROUND,
    ANIMATION
}

// Estilo de burbuja de chat
data class BubbleStyle(
    val shape: Shape = RoundedCornerShape(8.dp),
    val color: Color = Color(0xFF6200EE),
    val borderStroke: BorderStroke? = null,
    val shadowElevation: Int = 4
)

// Estilos predefinidos de burbujas
object BubbleStyles {
    val DEFAULT = BubbleStyle(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF6200EE)
    )
    
    val NEON = BubbleStyle(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF00E5FF),
        borderStroke = BorderStroke(2.dp, Color(0xFF00BCD4)),
        shadowElevation = 8
    )
    
    val GOLDEN = BubbleStyle(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFD700),
        borderStroke = BorderStroke(3.dp, Color(0xFFFF8F00)),
        shadowElevation = 12
    )
    
    val CLOUD = BubbleStyle(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 4.dp, bottomStart = 20.dp),
        color = Color(0xFFE1F5FE)
    )
    
    val SQUARE = BubbleStyle(
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFF9C27B0)
    )
    
    val GRADIENT_PURPLE = BubbleStyle(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF7B1FA2)
    )
    
    val GRADIENT_BLUE = BubbleStyle(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1976D2)
    )
    
    val GRADIENT_GREEN = BubbleStyle(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF388E3C)
    )
    
    val GRADIENT_RED = BubbleStyle(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFD32F2F)
    )
    
    val GLASS = BubbleStyle(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x80FFFFFF),
        borderStroke = BorderStroke(1.dp, Color(0x40FFFFFF))
    )
}

// Función para obtener estilo de burbuja por ID
fun getBubbleStyleById(id: Int): BubbleStyle {
    return when (id) {
        1 -> BubbleStyles.DEFAULT
        2 -> BubbleStyles.NEON
        3 -> BubbleStyles.GOLDEN
        4 -> BubbleStyles.CLOUD
        5 -> BubbleStyles.SQUARE
        6 -> BubbleStyles.GRADIENT_PURPLE
        7 -> BubbleStyles.GRADIENT_BLUE
        8 -> BubbleStyles.GRADIENT_GREEN
        9 -> BubbleStyles.GRADIENT_RED
        10 -> BubbleStyles.GLASS
        else -> BubbleStyles.DEFAULT
    }
}
