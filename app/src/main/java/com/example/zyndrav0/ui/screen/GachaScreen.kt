package com.example.zyndrav0.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.zyndrav0.R
import com.example.zyndrav0.viewmodel.GachaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaScreen(navController: NavController, viewModel: GachaViewModel = viewModel()) {
    val currency by viewModel.currency.collectAsState()
    val lastPullResult by viewModel.lastPullResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val pullCost = 5
    val multiPullCost = 50

    LaunchedEffect(lastPullResult) {
        if (lastPullResult.isNotEmpty()) {
            val itemIds = lastPullResult.joinToString(",") { it.id.toString() }
            navController.navigate("gacha_result/$itemIds")
            viewModel.clearLastPull()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invocación Gacha") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.logo), // Placeholder
                        contentDescription = "Banner Gacha",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 200f)
                            )
                    )
                    Text(
                        text = "¡Burbujas de Chat Legendarias!",
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    Icons.Filled.MonetizationOn,
                    contentDescription = "Moneda",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = currency.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical= 16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.pullGacha(1) },
                    enabled = currency >= pullCost && !isLoading,
                    modifier = Modifier.weight(1f).height(60.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Tirar x1\n($pullCost Monedas)", textAlign = TextAlign.Center)
                }

                Button(
                    onClick = { viewModel.pullGacha(10) },
                    enabled = currency >= multiPullCost && !isLoading,
                    modifier = Modifier.weight(1f).height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Tirar x10\n($multiPullCost Monedas)", textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
