package com.example.zyndrav0

import androidx.compose.ui.graphics.Color
import com.example.zyndrav0.viewmodel.SettingsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        viewModel = SettingsViewModel()
    }

    @Test
    fun `el tema oscuro empieza desactivado y cambia al alternar`() {
        // false
        assertFalse("Debería empezar en modo claro", viewModel.isDarkTheme.value)

        // tamo activos
        viewModel.toggleDarkTheme()
        assertTrue("Debería haber cambiado a modo oscuro", viewModel.isDarkTheme.value)

        // activon't
        viewModel.toggleDarkTheme()
        assertFalse("Debería haber vuelto a modo claro", viewModel.isDarkTheme.value)
    }

    @Test
    fun `cambiar color de burbuja de usuario funciona`() {
        // morao inicial
        val colorInicial = Color(0xFF6200EE)
        assertEquals(colorInicial, viewModel.userBubbleColor.value)

        // ahora moradon't
        val nuevoColor = Color.Red
        viewModel.updateUserBubbleColor(nuevoColor)

        // THEN
        assertEquals(nuevoColor, viewModel.userBubbleColor.value)
    }

    @Test
    fun `cambiar color de burbuja de asistente funciona`() {
        // GIVEN
        val colorInicial = Color(0xFFE0E0E0)
        assertEquals(colorInicial, viewModel.assistantBubbleColor.value)

        // Green
        val nuevoColor = Color.Green
        viewModel.updateAssistantBubbleColor(nuevoColor)

        // THEN
        assertEquals(nuevoColor, viewModel.assistantBubbleColor.value)
    }
}