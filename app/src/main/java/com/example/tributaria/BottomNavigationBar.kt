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


@Composable
fun BottomNavigationBar(navController: NavController) {
    // Navigation bar container
    NavigationBar {
        // Home navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = false,
            onClick = {navController.navigate("success") {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true

            }}
        )

        // Gemini IA navigation item with a custom image icon
        NavigationBarItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_gemini),
                    contentDescription = "Gemini IA",
                    modifier = Modifier.size(24.dp) // Adjust the size as needed
                )
            },
            label = { Text("Gemini IA") },
            selected = false,
            onClick = {}
        )

        // Calendar navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Event, contentDescription = "Calendario") },
            label = { Text("Calendario") },
            selected = false,
            onClick = {}
        )

        // Forum navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.ChatBubble, contentDescription = "Foro") },
            label = { Text("Foro") },
            selected = false,
            onClick = {}
        )
    }
}
