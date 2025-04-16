package com.example.tributaria.features.recoveraccount


import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.BorderStroke

import androidx.navigation.NavHostController


@Composable
fun RecoverScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Title
        Text(
            text = "Recuperar contraseña",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2271B3)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Description
        Text(
            text = "Si tu correo está registrado, te enviaremos un código de verificación.",
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Email label
        Text(
            text = "Ingresa tu correo registrado",
            color = Color(0xFF2271B3),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            },
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email Icon")
            },
            modifier = Modifier.fillMaxWidth(),
            isError = !isEmailValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Error message if email is invalid
        if (!isEmailValid) {
            Text(
                text = "Por favor ingresa un correo válido",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Send Code Button
        Button(
            onClick = {
                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                if (isEmailValid && email.isNotBlank()) {

                    navController.navigate("verification") // Navigate to verification screen
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2271B3)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Enviar código", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Back Button
        OutlinedButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("recover") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("Regresar", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
