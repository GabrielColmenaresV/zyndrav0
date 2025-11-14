package com.example.zyndrav0.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BluetoothDeviceUi(
    val name: String,
    val address: String,
    val bondState: Int
)

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothAdapter: BluetoothAdapter? =
        (application.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private val _devices = MutableStateFlow<List<BluetoothDeviceUi>>(emptyList())
    val devices: StateFlow<List<BluetoothDeviceUi>> = _devices

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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

    fun startScan() {
        viewModelScope.launch {
            if (bluetoothAdapter == null) {
                _errorMessage.value = "Bluetooth no disponible en este dispositivo."
                return@launch
            }
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }
            discoveredAddresses.clear()
            _devices.value = emptyList()
            _errorMessage.value = null
            _isScanning.value = bluetoothAdapter.startDiscovery()
            if (!_isScanning.value) {
                _errorMessage.value = "No se pudo iniciar la búsqueda."
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
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
        getApplication<Application>().registerReceiver(receiver, filter)
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
        runCatching { getApplication<Application>().unregisterReceiver(receiver) }
    }
}

