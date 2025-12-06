package com.example.zyndrav0.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionManager {
    
    // Permisos de cámara
    val CAMERA_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA
    )
    
    // Permisos de almacenamiento segun la version de Android
    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    
    // Permisos de Bluetooth
    val BLUETOOTH_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }
    
    fun hasCameraPermission(context: Context): Boolean {
        return CAMERA_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun hasStoragePermission(context: Context): Boolean {
        return STORAGE_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun hasBluetoothPermission(context: Context): Boolean {
        return BLUETOOTH_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
