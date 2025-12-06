package com.example.zyndrav0.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.example.zyndrav0.util.PermissionManager

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FilePickerDialog(
    onImageSelected: (Uri) -> Unit,
    onFileSelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val storagePermissionState = rememberMultiplePermissionsState(
        permissions = PermissionManager.STORAGE_PERMISSIONS.toList()
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it)
            onDismiss()
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onFileSelected(it)
            onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        if (!storagePermissionState.allPermissionsGranted) {
            storagePermissionState.launchMultiplePermissionRequest()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar archivo") },
        text = {
            if (storagePermissionState.allPermissionsGranted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar imagen")
                    }

                    Button(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar archivo")
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Se requiere permiso de almacenamiento")
                    Button(onClick = { storagePermissionState.launchMultiplePermissionRequest() }) {
                        Text("Conceder permiso")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
