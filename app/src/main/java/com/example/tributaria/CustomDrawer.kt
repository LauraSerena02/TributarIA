package com.example.tributaria

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun CustomDrawer(navController: NavController, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color.White) // Gradient background
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // User avatar
        Image(
            painter = painterResource(id = R.drawable.avatar_placeholder),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // User name
        Text("Admin", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Spacer(modifier = Modifier.height(10.dp))

        // App name and version
        Box(
            modifier = Modifier
                .border(1.dp, Color.White, shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("TributarIA", color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Versión: 1.0.0", fontSize = 12.sp, color = Color.White)

        Spacer(modifier = Modifier.height(30.dp))

        // Navigation buttons
        DrawerButton("Inicio", Icons.Default.Home) {
            navController.navigate("success") {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
            }
        }
        DrawerButton("Asistencia financiera", Icons.Default.AttachMoney) {
            navController.navigate("financial_assistance") {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
            }
        }
        DrawerButton("Asistencia tributaria", Icons.Default.AccountBalance) {
            navController.navigate("tax_assistance") {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
            }
        }
        DrawerButton("Configuración", Icons.Default.Settings) {
            navController.navigate("settings") {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
            }
        }
        DrawerButton("Cerrar Sesión", Icons.AutoMirrored.Filled.Logout) {
            onLogout()  // Ejecuta la lógica de cierre de sesión si es necesario
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true } // Elimina todas las pantallas previas
                launchSingleTop = true
            }
        }

        Spacer(modifier = Modifier.height(200.dp))

        // Footer
        Text("Creado por", fontSize = 12.sp, color = Color.Black)
        Text("TRICODE STUDIO", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
    }
}

// 🔒 Private function for drawer buttons
@Composable
private fun DrawerButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)), // Custom button color
        shape = RoundedCornerShape(12.dp), // Rounded corners
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, fontSize = 16.sp)
        }
    }
}
