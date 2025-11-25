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
fun HeaderContentMoneyLend(onMenuClick: () -> Unit) {

    // Estado para mostrar el modal
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color(0xFF1E40AF))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Botón del menú
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
            Icon(Icons.Default.Info, contentDescription = "¿Qué es la amortización de créditos?", tint = Color.White)
        }

        // Textos del header
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Calculadora amortización de créditos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Calcula cuánto debes pagar de intereses.",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Modal explicando qué es amortización
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Qué es la amortización de créditos?") },
            text = {
                Column {
                    Text(
                        "La amortización de un crédito es el proceso mediante el cual " +
                                "vas pagando poco a poco un préstamo a lo largo del tiempo.\n\n" +
                                "Cada cuota que pagas está compuesta por:\n" +
                                "• Una parte que reduce la deuda (capital).\n" +
                                "• Una parte que corresponde a intereses.\n\n" +
                                "La amortización te permite saber cuánto pagarás en total, " +
                                "cómo se distribuyen las cuotas y cuántos intereses terminarás pagando."
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
