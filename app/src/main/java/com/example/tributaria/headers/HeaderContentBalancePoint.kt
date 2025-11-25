package com.example.tributaria.headers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderContentBalancePoint(onMenuClick: () -> Unit) {

    // Estado del modal
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color(0xFF1E40AF))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {

        // Botón menú
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 25.dp)
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
        }

        // Botón de información
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = 25.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = "¿Qué es el punto de equilibrio?", tint = Color.White)
        }

        // Texto principal
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Calculadora punto de equilibrio",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Calcula cuando tus ingresos cubren los costos.",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Modal explicando el punto de equilibrio
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Qué es el punto de equilibrio?") },
            text = {
                Column {
                    Text(
                        "El punto de equilibrio es el nivel de ventas en el que una empresa " +
                                "ni gana ni pierde.\n\n" +
                                "Es decir, es el momento exacto en el que los ingresos totales son " +
                                "igual a los costos totales.\n\n" +
                                "En otras palabras:\n" +
                                "• Si vendes menos del punto de equilibrio → pierdes dinero.\n" +
                                "• Si vendes más del punto de equilibrio → generas ganancias.\n\n" +
                                "Es una herramienta esencial para saber cuántas unidades debes vender " +
                                "para cubrir tus costos fijos y variables."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
