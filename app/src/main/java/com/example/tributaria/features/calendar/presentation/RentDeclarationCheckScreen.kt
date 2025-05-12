package com.example.tributaria.features.calendar.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentDeclarationCheckScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verificador de declaración de renta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Formulario de verificación
            Text("Selecciona tu tipo de persona:", style = MaterialTheme.typography.titleMedium)

            var selectedOption by remember { mutableStateOf("") }

            RadioButtonRow(
                text = "Persona Natural",
                selected = selectedOption == "natural",
                onSelect = { selectedOption = "natural" }
            )

            RadioButtonRow(
                text = "Persona Jurídica",
                selected = selectedOption == "juridica",
                onSelect = { selectedOption = "juridica" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedOption.isNotEmpty()) {
                // Campos adicionales según tipo de persona
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Ingresos anuales") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Patrimonio bruto") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Lógica de verificación */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verificar obligación")
                }
            }
        }
    }
}

@Composable
private fun RadioButtonRow(text: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Text(text, modifier = Modifier.padding(start = 8.dp))
    }
}