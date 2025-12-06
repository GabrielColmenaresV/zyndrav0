package com.example.zyndrav0.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zyndrav0.viewmodel.CoinBundle
import com.example.zyndrav0.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(viewModel: StoreViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isProcessing by viewModel.isProcessing.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda de Monedas") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isProcessing) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Procesando compra...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.bundles) { bundle ->
                    BundleCard(bundle = bundle, onClick = { viewModel.purchaseCoins(bundle) })
                }
                item {
                    Text(
                        "Las compras son simuladas y únicamente añaden monedas virtuales para el sistema gacha.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BundleCard(bundle: CoinBundle, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(bundle.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Incluye ${bundle.amount} monedas para usar en el gacha.", style = MaterialTheme.typography.bodyMedium)
            Text("Precio sugerido: ${bundle.priceLabel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Comprar")
            }
        }
    }
}

