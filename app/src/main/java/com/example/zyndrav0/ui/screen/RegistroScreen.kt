package com.example.zyndrav0.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.zyndrav0.viewmodel.RegistroViewModel
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(viewModel: RegistroViewModel, navController: NavHostController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordFieldRegistro(
                label = "Contraseña",
                password = viewModel.password,
                onPasswordChange = { viewModel.password = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordFieldRegistro(
                label = "Confirmar contraseña",
                password = viewModel.confirmPassword,
                onPasswordChange = { viewModel.confirmPassword = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        viewModel.register {
                            scope.launch {
                                snackbarHostState.showSnackbar("✅ Registro completado con éxito")
                                navController.navigate("main") {
                                    popUpTo("registro") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                } else {
                    Text("Registrar")
                }
            }

            if (viewModel.errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(viewModel.errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

@Composable
fun PasswordFieldRegistro(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else
                Icons.Filled.VisibilityOff

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = image,
                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        }
    )
}