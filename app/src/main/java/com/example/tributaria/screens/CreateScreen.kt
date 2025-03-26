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



import android.util.Patterns

@Composable
fun CreateScreen(navController: NavHostController) {

    // State variables
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var passwordMismatch by remember { mutableStateOf(false) }
    var isEmailValid by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Crear cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2271B3)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Organiza tus impuestos y toma el control de tus finanzas. ¡Regístrate ahora!",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(it).matches()
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isEmailValid
        )
        if (!isEmailValid) {
            Text(
                text = "Por favor ingresa un correo válido",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

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

        if (passwordMismatch) {
            Text(
                text = "Las contraseñas no coinciden",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        var showTermsDialog by remember { mutableStateOf(false) }

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
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "TributarIA, en cumplimiento de la Ley 1581 de 2012 y el Decreto Reglamentario 1377 de 2013, implementa sus Políticas para el Tratamiento y Protección de Datos Personales.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "TributarIA implementará acciones necesarias para garantizar la protección de los datos personales.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Se protegerán los derechos a la privacidad, intimidad y buen nombre.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Estas políticas aplican tanto a datos personales como transaccionales.",
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

        Button(
            onClick = {
                passwordMismatch = password != confirmPassword
                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                if (!passwordMismatch && isEmailValid) {
                    navController.navigate("login") {
                        popUpTo("create") { inclusive = true }
                        launchSingleTop = true //Add logic to add to database
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
