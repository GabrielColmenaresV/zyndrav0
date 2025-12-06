package com.example.zyndrav0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyndrav0.data.repository.BluetoothDeviceUi
import com.example.zyndrav0.data.repository.BluetoothRepository
import com.example.zyndrav0.data.repository.ZyndraBluetoothRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BluetoothViewModel(
    application: Application,
    private val repository: BluetoothRepository = ZyndraBluetoothRepository(application)
) : AndroidViewModel(application) {

    val devices: StateFlow<List<BluetoothDeviceUi>> = repository.devices
    val isScanning: StateFlow<Boolean> = repository.isScanning
    val errorMessage: StateFlow<String?> = repository.errorMessage

    fun startScan() {
        repository.startScan()
    }

    fun stopScan() {
        repository.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}