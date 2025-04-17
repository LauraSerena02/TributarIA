package com.example.tributaria.features.recoveraccount.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tributaria.features.recoveraccount.viewmodel.RecoverAccountViewModel

@Composable
fun RecoverScreen(navController: NavHostController, viewModel: RecoverAccountViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissDialog()
                    navController.navigate("login") {
                        popUpTo("recover") { inclusive = true }
                        launchSingleTop = true
                    }
                }) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("¡Correo enviado!", fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text("Hemos enviado un enlace a tu correo para que puedas restablecer tu contraseña.", textAlign = TextAlign.Center, fontSize = 14.sp)
                }
            },
            containerColor = Color.White,
            shape = MaterialTheme.shapes.medium
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))
        Text("Recuperar contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2271B3))
        Spacer(Modifier.height(10.dp))
        Text("Si tu correo está registrado, te enviaremos un código de verificación.", textAlign = TextAlign.Center, fontSize = 14.sp)
        Spacer(Modifier.height(15.dp))
        Text("Ingresa tu correo registrado", color = Color(0xFF2271B3), fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            modifier = Modifier.fillMaxWidth(),
            isError = !state.isEmailValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        if (!state.isEmailValid) {
            Text("Por favor ingresa un correo válido", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = state.errorMessage ?: "", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.sendPasswordReset { /* puedes usarlo si necesitas */ } },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2271B3)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Enviar código", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("recover") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("Regresar", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
