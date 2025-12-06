package com.example.zyndrav0.data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BluetoothDeviceUi(
    val name: String,
    val address: String,
    val bondState: Int
)

interface BluetoothRepository {
    val devices: StateFlow<List<BluetoothDeviceUi>>
    val isScanning: StateFlow<Boolean>
    val errorMessage: StateFlow<String?>

    fun startScan()
    fun stopScan()
    fun cleanup()
}

class ZyndraBluetoothRepository(private val context: Context) : BluetoothRepository {

    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private val _devices = MutableStateFlow<List<BluetoothDeviceUi>>(emptyList())
    override val devices: StateFlow<List<BluetoothDeviceUi>> = _devices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val discoveredAddresses = mutableSetOf<String>()

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { addDevice(it) }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isScanning.value = false
                }
            }
        }
    }

    init {
        registerReceiver()
        loadBondedDevices()
    }

    @SuppressLint("MissingPermission")
    private fun loadBondedDevices() {
        bluetoothAdapter?.bondedDevices?.forEach { addDevice(it) }
    }

    @SuppressLint("MissingPermission") // Suprimimos el error aquí
    override fun startScan() {
        if (bluetoothAdapter == null) {
            _errorMessage.value = "Bluetooth no disponible en este dispositivo."
            return
        }

        try {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }

            discoveredAddresses.clear()
            _devices.value = emptyList()
            _errorMessage.value = null

            loadBondedDevices()

            if (!bluetoothAdapter.startDiscovery()) {
                _errorMessage.value = "No se pudo iniciar la búsqueda."
                _isScanning.value = false
            } else {
                _isScanning.value = true
            }
        } catch (e: SecurityException) {
            _errorMessage.value = "Faltan permisos de Bluetooth."
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        try {
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter.cancelDiscovery()
            }
        } catch (e: SecurityException) {
            // Ignorar si no hay permiso
        }
        _isScanning.value = false
    }

    @SuppressLint("MissingPermission")
    private fun addDevice(device: BluetoothDevice) {
        val address = device.address ?: return
        if (discoveredAddresses.add(address)) {
            val uiModel = BluetoothDeviceUi(
                name = device.name ?: "Dispositivo sin nombre",
                address = address,
                bondState = device.bondState
            )
            _devices.value = _devices.value + uiModel
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)
    }

    override fun cleanup() {
        try {
            stopScan()
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}