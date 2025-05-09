package com.example.tributaria

import androidx.annotation.OptIn
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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Calculate
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.tributaria.features.login.viewmodel.LoginViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.unit.TextUnit
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun CustomDrawer(
    navController: NavController,
    onLogout: () -> Unit
) {
    val loginViewModel: LoginViewModel = hiltViewModel()


    // Estado para controlar la visibilidad de los mini botones
    var showFinancialOptions by remember { mutableStateOf(false) }

    val userName by loginViewModel.userName.collectAsState(initial = "Cargando...")


    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color.White)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Avatar de usuario
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

        // Nombre del usuario
        Text(
            text = userName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

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
                popUpTo("success") { inclusive = false }
                launchSingleTop = true
            }
        }
        DrawerButton("Asistencia financiera", Icons.Default.AttachMoney) {
            // Toggle the visibility of the mini buttons
            showFinancialOptions = !showFinancialOptions
        }
        // Show the mini buttons when showFinancialOptions is true
        if (showFinancialOptions) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                // Punto de equilibrio con tamaño de texto más pequeño
                DrawerButtonWithCustomFont("Punto de equilibrio", Icons.AutoMirrored.Filled.ShowChart, fontSize = 14.sp) {
                    navController.navigate("balance_point") {
                        popUpTo("success") { inclusive = true }
                        launchSingleTop = true }
                }

                // Calculadora VAN y TIR con tamaño de texto más pequeño
                DrawerButtonWithCustomFont("Calculadora VAN y TIR", Icons.Rounded.Calculate, fontSize = 14.sp) {
                    navController.navigate("van_tir") {
                        popUpTo("success") { inclusive = true }
                        launchSingleTop = true }
                }

                // Préstamos Bancarios con tamaño de texto más pequeño
                DrawerButtonWithCustomFont("Préstamos Bancarios", Icons.Default.Payments, fontSize = 14.sp) {
                    navController.navigate("amortization_calculation") {
                        popUpTo("success") { inclusive = true }
                        launchSingleTop = true }
                }
            }
        }

        DrawerButton("Asistencia tributaria", Icons.Default.AccountBalance) {
            navController.navigate("tax_assistance") {
                popUpTo("success") { inclusive = true }
                launchSingleTop = true
            }
        }
        DrawerButton("Configuración", Icons.Default.Settings) {
            navController.navigate("settings") {
                popUpTo("success") { inclusive = true }
                launchSingleTop = true
            }
        }
        DrawerButton("Cerrar Sesión", Icons.AutoMirrored.Filled.Logout) {
            // Llama a la función de logout desde la instancia del ViewModel
            loginViewModel.logout()

            // Navegar al login después de cerrar sesión
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true }
        }

        Spacer(modifier = Modifier.height(30.dp))

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

@Composable
private fun DrawerButtonWithCustomFont(text: String, icon: ImageVector, fontSize: TextUnit, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            // Ahora usamos fontSize específico aquí
            Text(text, color = Color.White, fontSize = fontSize)
        }
    }
}