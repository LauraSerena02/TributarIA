package com.example.tributaria

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavController) {
    // Obtener la ruta actual para resaltar el ítem seleccionado
    val currentRoute = navController.currentDestination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = currentRoute == "success",
            onClick = {
                navController.navigate("success") {
                    popUpTo("success") { inclusive = false }
                    launchSingleTop = true
                }
            }
        )

        NavigationBarItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_gemini),
                    contentDescription = "Gemini IA",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Gemini IA") },
            selected = currentRoute == "gemini_ia",
            onClick = {
                navController.navigate("gemini_ia") {
                    popUpTo("success") { inclusive = false }
                    launchSingleTop = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Event, contentDescription = "Calendario") },
            label = { Text("Calendario") },
            selected = currentRoute == "calendar",
            onClick = {
                navController.navigate("calendar") {
                    popUpTo("success") { inclusive = false }
                    launchSingleTop = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.ChatBubble, contentDescription = "Foro") },
            label = { Text("Foro") },
            selected = currentRoute == "foro",
            onClick = {
                navController.navigate("foro") {
                    popUpTo("success") { inclusive = false }
                    launchSingleTop = true
                }
            }
        )
    }
}