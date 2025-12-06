package com.example.zyndrav0.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zyndrav0.viewmodel.ChatViewModel

private val DefaultLightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)


fun dynamicLightColorScheme(seedColor: Color): androidx.compose.material3.ColorScheme {
    return lightColorScheme(
        primary = seedColor,
        secondary = Color(seedColor.red * 0.8f, seedColor.green * 0.8f, seedColor.blue * 0.8f),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
    )
}

@Composable
fun ZyndraAppTheme(
    content: @Composable () -> Unit
) {
    val chatViewModel: ChatViewModel = viewModel()
    val equippedThemeColor by chatViewModel.equippedThemeColor.collectAsState()

    val colorScheme = equippedThemeColor?.let { colorString ->
        try {
            dynamicLightColorScheme(Color(android.graphics.Color.parseColor(colorString)))
        } catch (e: IllegalArgumentException) {
            DefaultLightColorScheme
        }
    } ?: DefaultLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}