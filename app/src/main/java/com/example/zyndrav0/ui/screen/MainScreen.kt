package com.example.zyndrav0.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zyndrav0.viewmodel.*

// Sealed class para la navegación interna de MainScreen
sealed class MainScreenTab(val route: String, val label: String, val icon: ImageVector) {
    object Chat : MainScreenTab("chat", "Chat", Icons.Default.Chat)
    object History : MainScreenTab("history", "Historial", Icons.Default.History)
    object Gacha : MainScreenTab("gacha", "Gacha", Icons.Default.Store)
    object Profile : MainScreenTab("profile", "Perfil", Icons.Default.Person)
    object Inventory : MainScreenTab("inventory", "Inventario", Icons.Default.Inventory)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val bottomNavItems = listOf(
        MainScreenTab.Chat,
        MainScreenTab.History,
        MainScreenTab.Gacha,
        MainScreenTab.Profile,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = MainScreenTab.Chat.route, modifier = Modifier.padding(innerPadding)) {
            composable(MainScreenTab.Chat.route) {
                val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory(application, -1L))
                ChatScreen(viewModel = chatViewModel)
            }
            composable(MainScreenTab.History.route) {
                val chatHistoryViewModel: ChatHistoryViewModel = viewModel()
                ChatHistoryScreen(viewModel = chatHistoryViewModel, onConversationClick = { /* TODO */ })
            }
            composable(MainScreenTab.Gacha.route) { 
                GachaScreen(navController = navController) 
            }
            composable(MainScreenTab.Profile.route) { 
                ProfileScreen(
                    viewModel = viewModel<ProfileViewModel>(),
                    onLogoutClick = onLogout,
                    onNavigateToInventory = { navController.navigate(MainScreenTab.Inventory.route) },
                    onNavigateToBluetooth = { navController.navigate("bluetooth") },
                    onNavigateToStore = { navController.navigate("store") }
                )
            }
            composable(MainScreenTab.Inventory.route) {
                InventoryScreen(viewModel<InventoryViewModel>())
            }
             composable("gacha_result/{itemIds}") { backStackEntry ->
                val gachaViewModel: GachaViewModel = viewModel()
                val itemIdsString = backStackEntry.arguments?.getString("itemIds") ?: ""
                val itemIds = itemIdsString.split(",").mapNotNull { it.toIntOrNull() }
                GachaResultScreen(navController = navController, itemIds = itemIds, viewModel = gachaViewModel)
            }
            composable("bluetooth") {
                BluetoothScreen(viewModel = viewModel())
            }
            composable("store") {
                StoreScreen(viewModel = viewModel())
            }
        }
    }
}