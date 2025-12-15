package com.example.zyndrav0.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.zyndrav0.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToBluetooth: () -> Unit,
    onNavigateToStore: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()
    val inventoryCount by viewModel.inventoryCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let { viewModel.updateProfileImage(it) } }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                user == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "No se pudo cargar la información del usuario",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Por favor, cierra sesión y vuelve a iniciar sesión",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.logout()
                                    onLogoutClick()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user?.profileImageUri != null) {
                                AsyncImage(
                                    model = user?.profileImageUri,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Person, contentDescription = "Foto de perfil", modifier = Modifier.size(60.dp))
                            }
                        }

                        Text("Toca para cambiar foto", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                ProfileInfoRow(icon = Icons.Default.Person, label = "Nombre", value = user?.username ?: "N/A")


                                ProfileInfoRow(icon = Icons.Default.MonetizationOn, label = "Monedas", value = user?.currency?.toString() ?: "0")
                                ProfileInfoRow(icon = Icons.Default.Star, label = "Nivel", value = user?.level?.toString() ?: "1")
                                ProfileInfoRow(icon = Icons.Default.Inventory, label = "Ítems del Gacha", value = inventoryCount.toString())
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = onNavigateToInventory,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver Inventario")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onNavigateToBluetooth,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Bluetooth, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Escanear Bluetooth")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onNavigateToStore,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Comprar Monedas")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.logout()
                                    onLogoutClick()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrar Sesión")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}