package com.example.tributaria.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun FailureScreen(navController: NavHostController) {
    // Column layout to center the content both vertically and horizontally
    Column(
        modifier = Modifier
            .fillMaxSize() // Fills the entire screen
            .padding(16.dp), // Adds padding around the content
        horizontalAlignment = Alignment.CenterHorizontally, // Centers content horizontally
        verticalArrangement = Arrangement.Center // Centers content vertically
    ) {
        // Display the error message when the login fails
        Text(
            text = "Datos de usuario incorrectos, ¿Seguro tienes una cuenta de usuario?",
            modifier = Modifier.padding(bottom = 24.dp) // Adds space below the text
        )

        // Button that navigates back to the login screen
        Button(onClick = {
            navController.navigate("login") {
                // Removes the failure screen from the back stack to avoid stacking
                popUpTo("failure") { inclusive = true }
                // Ensures only one instance of the login screen is launched
                launchSingleTop = true
            }
        }) {
            // Button text
            Text("Volver al inicio de sesión")
        }
    }

}
