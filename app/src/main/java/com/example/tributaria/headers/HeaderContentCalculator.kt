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
fun HeaderCalculatorVanTir(onMenuClick: () -> Unit) {

    // Estado para mostrar u ocultar el dialog
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color(0xFF1E40AF))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {

        // Botón del menú (hamburger)
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
            Icon(Icons.Default.Info, contentDescription = "¿Qué es VAN y TIR?", tint = Color.White)
        }

        // Contenido del header
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Calculadora de VAN y TIR",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Calculadora de valor actual neto y tasa interna de retorno.",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Modal explicando VAN y TIR
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            },
            title = {
                Text("¿Qué son VAN y TIR?")
            },
            text = {
                Column {
                    Text(
                        "• VAN (Valor Actual Neto):\n" +
                                "Es una medida que permite saber si un proyecto es rentable. " +
                                "Calcula cuánto valen hoy los flujos de dinero futuros."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "• TIR (Tasa Interna de Retorno):\n" +
                                "Es la tasa de rendimiento que genera un proyecto. " +
                                "Si la TIR es mayor al costo de oportunidad, el proyecto es viable."
                    )
                }
            }
        )
    }
}
