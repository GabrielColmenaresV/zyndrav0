package com.example.zyndrav0

import android.app.Application
import com.example.zyndrav0.data.repository.BluetoothDeviceUi
import com.example.zyndrav0.data.repository.BluetoothRepository
import com.example.zyndrav0.viewmodel.BluetoothViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class BluetoothViewModelTest {

    @Mock lateinit var applicationFalsa: Application
    @Mock lateinit var repoFalso: BluetoothRepository

    private val fakeDevicesFlow = MutableStateFlow<List<BluetoothDeviceUi>>(emptyList())
    private val fakeScanningFlow = MutableStateFlow(false)
    private val fakeErrorFlow = MutableStateFlow<String?>(null)

    private lateinit var viewModel: BluetoothViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        Mockito.`when`(repoFalso.devices).thenReturn(fakeDevicesFlow)
        Mockito.`when`(repoFalso.isScanning).thenReturn(fakeScanningFlow)
        Mockito.`when`(repoFalso.errorMessage).thenReturn(fakeErrorFlow)

        viewModel = BluetoothViewModel(applicationFalsa, repoFalso)
    }

    @Test
    fun `al iniciar escaneo llama al repositorio`() {
        // WHEN
        viewModel.startScan()

        // THEN
        Mockito.verify(repoFalso).startScan()
    }

    @Test
    fun `al detener escaneo llama al repositorio`() {
        // WHEN
        viewModel.stopScan()

        // THEN
        Mockito.verify(repoFalso).stopScan()
    }

    @Test
    fun `los dispositivos del repositorio se reflejan en el viewModel`() {
        // GIVEN
        val dispositivo = BluetoothDeviceUi("Auriculares", "00:11:22:33:44", 12)
        fakeDevicesFlow.value = listOf(dispositivo)

        // THEN
        assertEquals(1, viewModel.devices.value.size)
        assertEquals("Auriculares", viewModel.devices.value[0].name)
    }

    @Test
    fun `estado de escaneo se refleja correctamente`() {
        fakeScanningFlow.value = true

        // THEN
        assertTrue(viewModel.isScanning.value)
    }
}