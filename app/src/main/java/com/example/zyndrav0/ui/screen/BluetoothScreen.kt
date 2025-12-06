package com.example.zyndrav0.ui.screen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zyndrav0.viewmodel.BluetoothDeviceUi
import com.example.zyndrav0.viewmodel.BluetoothViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothScreen(viewModel: BluetoothViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    var permissionsGranted by remember {
        mutableStateOf(
            permissions.all {
                ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
        )
    }
    var shouldWarnPermissions by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grantedMap ->
        permissionsGranted = grantedMap.values.all { it }
        shouldWarnPermissions = !permissionsGranted
    }

    val devices by viewModel.devices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(shouldWarnPermissions) {
        if (shouldWarnPermissions) {
            snackbarHostState.showSnackbar("Necesitas conceder permisos Bluetooth/ubicación.")
            shouldWarnPermissions = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dispositivos Bluetooth") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Escanea dispositivos Bluetooth cercanos. Esta versión sólo lista los dispositivos; no se conecta automáticamente.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    if (!permissionsGranted) {
                        permissionLauncher.launch(permissions)
                    } else {
                        viewModel.startScan()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isScanning
            ) {
                Icon(Icons.Default.Bluetooth, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isScanning) "Buscando..." else "Buscar dispositivos")
            }

            if (isScanning) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    Text("Escaneando...", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (devices.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No se encontraron dispositivos todavía.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(devices) { device ->
                        BluetoothDeviceRow(device)
                    }
                }
            }
        }
    }
}

@Composable
private fun BluetoothDeviceRow(device: BluetoothDeviceUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(device.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(device.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        val bondLabel = when (device.bondState) {
            android.bluetooth.BluetoothDevice.BOND_BONDED -> "Emparejado"
            android.bluetooth.BluetoothDevice.BOND_BONDING -> "Emparejando..."
            else -> "Sin emparejar"
        }
        Text(bondLabel, style = MaterialTheme.typography.bodySmall)
    }
}
