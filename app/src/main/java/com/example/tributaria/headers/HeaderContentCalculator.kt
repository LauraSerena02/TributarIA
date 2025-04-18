package com.example.tributaria.headers


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun HeaderCalculatorVanTir(onMenuClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)// Change the height of the card
            .background(Color(0xFF1E40AF)) // Dark blue background
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Menu button (hamburger icon)
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 25.dp) // Moves the icon further down
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
        }

        // Row containing the title and subtitle
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f)) // Pushes text to the bottom
                Text("Calculadora de VAN y TIR", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Calculadora de valor actual neto y tasa interna de retorno .", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
