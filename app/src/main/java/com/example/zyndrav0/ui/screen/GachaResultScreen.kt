package com.example.zyndrav0.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.zyndrav0.model.GachaItem
import com.example.zyndrav0.model.Rarity
import com.example.zyndrav0.viewmodel.GachaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaResultScreen(navController: NavController, itemIds: List<Int>, viewModel: GachaViewModel = viewModel()) {
    val items = viewModel.getItemsByIds(itemIds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados del Gacha") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                GachaItemCard(item)
            }
        }
    }
}

@Composable
fun GachaItemCard(item: GachaItem) {
    val isLR = item.rarity == Rarity.LR
    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val backgroundColor = item.colorHex?.let { hex ->
        runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrNull()
    } ?: MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.aspectRatio(0.7f),
        colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = item.iconEmoji ?: "🎁",
                fontSize = 42.sp,
                textAlign = TextAlign.Center
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = item.rarity.color, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text(item.rarity.label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                item.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (isLR) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(6.dp)
                        .alpha(0.6f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✨", fontSize = 48.sp)
                }
            }
        }
    }
}