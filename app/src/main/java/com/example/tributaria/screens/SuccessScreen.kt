package com.example.tributaria.screens

// Required imports for layout, components, and navigation
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
fun SuccessScreen(navController: NavHostController) {
    // Main layout with vertical alignment and padding
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success message displayed on screen
        Text(
            text = "✅ Acceso exitoso a la pagina principal de TributarIA",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Button to navigate back to the Login screen
        Button(onClick = {
            navController.navigate("login") {
                // Remove SuccessScreen from the backstack to avoid stacking screens
                popUpTo("success") { inclusive = true }
                // Prevent multiple instances of the same screen
                launchSingleTop = true
            }
        }) {
            Text("Volver al inicio de sesion")
        }
    }
}
