package com.example.zyndrav0.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.zyndrav0.viewmodel.CoinPackage
import com.example.zyndrav0.viewmodel.SavedCard
import com.example.zyndrav0.viewmodel.StoreViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    onNavigateBack: () -> Unit,
    viewModel: StoreViewModel
) {
    val userCoins by viewModel.userCoins.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val purchaseStatus by viewModel.purchaseStatus.collectAsState()
    val savedCard by viewModel.savedCard.collectAsState()

    var selectedProduct by remember { mutableStateOf<CoinPackage?>(null) }

    fun formatCLP(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return format.format(amount)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda de Monedas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "$userCoins Monedas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text("Paquetes Disponibles (CLP)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.products) { product ->
                    ProductItem(
                        product = product,
                        priceFormatted = formatCLP(product.priceCLP),
                        onClick = { selectedProduct = product }
                    )
                }
            }
        }

        if (selectedProduct != null) {
            PaymentDialog(
                product = selectedProduct!!,
                initialCardData = savedCard,
                priceFormatted = formatCLP(selectedProduct!!.priceCLP),
                isProcessing = isProcessing,
                onDismiss = { selectedProduct = null },
                onConfirmPayment = { cardData ->
                    viewModel.processPayment(cardData, selectedProduct!!)
                }
            )
        }

        if (purchaseStatus != null) {
            val isSuccess = purchaseStatus!!.contains("EXITO")

            AlertDialog(
                onDismissRequest = {
                    viewModel.clearStatus()
                    if(isSuccess) selectedProduct = null
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearStatus()
                            if(isSuccess) selectedProduct = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Aceptar")
                    }
                },
                title = {
                    Text(
                        text = if (isSuccess) "¡Compra Exitosa!" else "Hubo un problema",
                        color = if (isSuccess) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                },
                text = { Text(purchaseStatus!!.replace("EXITO:", "").trim()) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(product: CoinPackage, priceFormatted: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${product.coinsAmount} Monedas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Button(onClick = onClick) {
                Text(text = priceFormatted)
            }
        }
    }
}

@Composable
fun PaymentDialog(
    product: CoinPackage,
    initialCardData: SavedCard,
    priceFormatted: String,
    isProcessing: Boolean,
    onDismiss: () -> Unit,
    onConfirmPayment: (SavedCard) -> Unit
) {
    var cardNumber by remember { mutableStateOf(initialCardData.number) }
    var cardHolder by remember { mutableStateOf(initialCardData.holderName) }
    var expiryDate by remember { mutableStateOf(initialCardData.expiry) }
    var cvv by remember { mutableStateOf(initialCardData.cvv) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pago Seguro", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Total: $priceFormatted", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = { cardHolder = it },
                    label = { Text("Nombre del Titular") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 16) cardNumber = it },
                    label = { Text("Número de Tarjeta (16 dígitos)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                    singleLine = true,
                    isError = cardNumber.isNotEmpty() && cardNumber.length < 16
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }

                            if (digits.length <= 4) {
                                expiryDate = if (digits.length >= 2) {
                                    digits.substring(0, 2) + "/" + digits.substring(2)
                                } else {
                                    digits
                                }
                            }
                        },
                        label = { Text("MM/AA") },
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Teclado solo números
                        singleLine = true,
                        placeholder = { Text("00/00") }
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { if (it.length <= 3) cvv = it },
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isProcessing) {
                    CircularProgressIndicator()
                    Text("Procesando pago...", modifier = Modifier.padding(top = 8.dp))
                } else {
                    Button(
                        onClick = {
                            onConfirmPayment(
                                SavedCard(
                                    holderName = cardHolder,
                                    number = cardNumber,
                                    expiry = expiryDate,
                                    cvv = cvv
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = cardNumber.length == 16 && cardHolder.isNotBlank() && cvv.length == 3
                    ) {
                        Text("Pagar $priceFormatted")
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}