package com.example.tributaria.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun CreateScreen(navController: NavHostController){

// State variables to store user input and UI states
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var passwordMismatch by remember { mutableStateOf(false) }

// Main column for layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Title text
        Text(
            text = "Crear cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2271B3)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Description text
        Text(
            text = "Organiza tus impuestos y toma el control de tus finanzas. ¡Regístrate ahora!",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Input fields
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Password mismatch warning
        if (passwordMismatch) {
            Text(
                text = "Las contraseñas no coinciden",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy terms dialog state
        var showTermsDialog by remember { mutableStateOf(false) }

        // Switch for accepting privacy terms
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF2271B3),
                    checkedTrackColor = Color(0xFFB0C5F0),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray
                )
            )

            Text(
                text = "Acepto los ",
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = "términos de privacidad.",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2271B3),
                modifier = Modifier.clickable { showTermsDialog = true }
            )
        }

        // Privacy terms dialog
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = {
                    Text(
                        "Términos de Privacidad",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2271B3)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "TributarIA, en cumplimiento de lo señalado en la Ley 1581 de 2012 y el Decreto Reglamentario 1377 de 2013, implementa sus Políticas para el Tratamiento y Protección de Datos Personales.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "TributarIA implementará todas las acciones necesarias para garantizar la protección y el tratamiento adecuado de los datos personales de los que sea responsable.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Se protegerán los derechos a la privacidad, la intimidad y el buen nombre, así como los derechos a conocer, actualizar y rectificar los datos de los titulares recogidos en las bases de datos propias.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Estas políticas aplican tanto para la protección de los datos personales como de la información transaccional tratada actualmente y en el futuro.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTermsDialog = false }) {
                        Text("Aceptar", color = Color(0xFF2271B3))
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Register button with validation
        Button(
            onClick = {
                passwordMismatch = password != confirmPassword
                if (!passwordMismatch) {
                    navController.navigate("login") {
                        popUpTo("create") { inclusive = true }
                        launchSingleTop = true //Database logic to be implemented
                    }
                }
            },
            enabled = termsAccepted && username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (termsAccepted) Color(0xFF2271B3) else Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Registrarme", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Back button
        OutlinedButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("create") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }

}