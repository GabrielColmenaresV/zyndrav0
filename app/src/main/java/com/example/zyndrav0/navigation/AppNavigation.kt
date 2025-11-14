package com.example.zyndrav0.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zyndrav0.ui.screen.InicioScreen
import com.example.zyndrav0.ui.screen.LoginScreen
import com.example.zyndrav0.ui.screen.MainScreen
import com.example.zyndrav0.ui.screen.RegistroScreen
import com.example.zyndrav0.viewmodel.InicioViewModel
import com.example.zyndrav0.viewmodel.LoginViewModel
import com.example.zyndrav0.viewmodel.RegistroViewModel

// Rutas de Navegacion Principal (Autenticación y App Principal)
sealed class AppScreen(val route: String) {
    object Inicio : AppScreen("inicio")
    object Login : AppScreen("login")
    object Registro : AppScreen("registro")
    object Main : AppScreen("main")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val inicioViewModel: InicioViewModel = viewModel()
    val isLoggedIn by inicioViewModel.isLoggedIn.collectAsState(initial = false)

    // Este NavHost solo se preocupa de si el usuario  autenticado o no.
    val startDestination = if (isLoggedIn) AppScreen.Main.route else AppScreen.Inicio.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppScreen.Inicio.route) {
            InicioScreen(navController = navController)
        }
        composable(AppScreen.Login.route) {
            // El LoginViewModel internamente actualiza la sesion.
            // El cambio en `isLoggedIn` dispara la navegaciin automticamente.
            LoginScreen(
                viewModel = viewModel<LoginViewModel>(),
                navController = navController,
                onLoginSuccess = {
                    // La navegacion se maneja automaticamente cuando isLoggedIn cambia
                    navController.navigate(AppScreen.Main.route) {
                        popUpTo(AppScreen.Inicio.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreen.Registro.route) {
            RegistroScreen(viewModel = viewModel<RegistroViewModel>(), navController = navController)
        }
        composable(AppScreen.Main.route) {
            MainScreen(
                onLogout = {
                    inicioViewModel.logout()
                    navController.navigate(AppScreen.Inicio.route) {
                        popUpTo(AppScreen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}