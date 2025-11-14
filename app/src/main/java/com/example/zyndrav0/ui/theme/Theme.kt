package com.example.zyndrav0.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkRedTheme = darkColorScheme(
    primary = RedCore,
    secondary = RedDark,
    background = BlackCore,
    surface = BlackSurface,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextGrey,
    error = Color(0xFFCF6679), // Un rojo brillante para errores
    primaryContainer = BlackSurface, // Contenedores de AppBar, etc.
    onPrimaryContainer = TextWhite,
    surfaceVariant = Color(0xFF2C2C2C), // Color para Cards y NavigationBar
    onSurfaceVariant = TextGrey
)

@Composable
fun ZyndraV0Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkRedTheme,
        typography = Typography,
        content = content
    )
}
