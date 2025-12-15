package com.example.zyndrav0.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _connectionStatus = MutableStateFlow("Desconectado")
    val connectionStatus: StateFlow<String> = _connectionStatus

    private var bluetoothSocket: BluetoothSocket? = null

    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    init {
        refreshPairedDevices()
    }

    fun refreshPairedDevices() {
        _connectionStatus.value = "Cargando dispositivos..."

        if (bluetoothAdapter == null) {
            _connectionStatus.value = "Bluetooth no soportado"
            return
        }

        try {
            if (bluetoothAdapter.isEnabled) {
                val bondedDevices = bluetoothAdapter.bondedDevices

                if (!bondedDevices.isNullOrEmpty()) {
                    _pairedDevices.value = bondedDevices.toList()
                    _connectionStatus.value = "Dispositivos cargados"
                } else {
                    _pairedDevices.value = emptyList()
                    _connectionStatus.value = "No hay dispositivos vinculados"
                }
            } else {
                _connectionStatus.value = "Bluetooth apagado"
            }
        } catch (e: SecurityException) {
            Log.e("BluetoothVM", "Falta permiso de Bluetooth (Scan/Connect): ${e.message}")
            _connectionStatus.value = "Falta permiso: BLUETOOTH_CONNECT"
            _pairedDevices.value = emptyList()
        } catch (e: Exception) {
            Log.e("BluetoothVM", "Error general: ${e.message}")
            _connectionStatus.value = "Error al cargar lista"
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            _connectionStatus.value = "Conectando a ${device.address}..."

            try {
                try {
                    bluetoothAdapter?.cancelDiscovery()
                } catch (e: SecurityException) {
                    Log.w("BluetoothVM", "No se pudo cancelar discovery (falta permiso)")
                }

                val socket = device.createRfcommSocketToServiceRecord(MY_UUID)

                socket.connect()

                bluetoothSocket = socket
                _isConnected.value = true
                _connectionStatus.value = "Conectado"

            } catch (e: SecurityException) {
                Log.e("BluetoothVM", "Error de Seguridad: ${e.message}")
                _connectionStatus.value = "Error: Falta permiso Bluetooth"
                _isConnected.value = false
            } catch (e: IOException) {
                Log.e("BluetoothVM", "Error IO: ${e.message}")
                _connectionStatus.value = "No se pudo conectar"
                _isConnected.value = false
                try {
                    bluetoothSocket?.close()
                } catch (closeEx: Exception) { }
            }
        }
    }

    // 3. Desconectar
    fun disconnect() {
        try {
            bluetoothSocket?.close()
            _isConnected.value = false
            _connectionStatus.value = "Desconectado"
        } catch (e: Exception) {
            Log.e("BluetoothVM", "Error al desconectar: ${e.message}")
        }
    }
}