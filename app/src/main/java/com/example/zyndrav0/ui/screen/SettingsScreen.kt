package com.example.zyndrav0.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//No integrada, futura entrega deberiamos sacarla
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBluetoothClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ajustes de la aplicación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            //  Apariencia
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Apariencia",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    SettingItem(
                        icon = Icons.Default.DarkMode,
                        title = "Modo oscuro",
                        subtitle = "Activar tema oscuro",
                        trailing = {
                            var isDarkMode by remember { mutableStateOf(false) }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { isDarkMode = it }
                            )
                        }
                    )

                    SettingItem(
                        icon = Icons.Default.ColorLens,
                        title = "Cosméticos",
                        subtitle = "Personaliza tu chat",
                        onClick = { /* TODO: Navegar a pantalla de cosméticos */ }
                    )
                }
            }

            // Sección de Conectividad
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Conectividad",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    SettingItem(
                        icon = Icons.Default.Bluetooth,
                        title = "Bluetooth",
                        subtitle = "Gestionar dispositivos Bluetooth",
                        onClick = onBluetoothClick
                    )
                }
            }

            // Notificaciones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    SettingItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        subtitle = "Activar notificaciones",
                        trailing = {
                            var notificationsEnabled by remember { mutableStateOf(true) }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        }
                    )
                }
            }

            // Sección Acerca de
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Acerca de",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    SettingItem(
                        icon = Icons.Default.Info,
                        title = "Versión",
                        subtitle = "Zyndra v1.0.0"
                    )
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
