package com.example.tributaria.features.calendar.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import com.example.tributaria.features.calendar.viewmodel.CalendarViewModel
import com.example.tributaria.headers.HeaderContentCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderConfigScreen(navController: NavController) {
    val viewModel: CalendarViewModel = hiltViewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var userType by remember { mutableStateOf("") }
    var userIdInput by remember { mutableStateOf("") }

    // Cargar configuración existente al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUserConfig()
        viewModel.userConfig.value?.let { (type, id) ->
            userType = type
            userIdInput = id
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar recordatorios") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            if (viewModel.showError.value == true) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissError() },
                    title = { Text("Error") },
                    text = { Text("No se pudieron programar los recordatorios. Por favor, intente nuevamente.") },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.dismissError() }
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de tipo de usuario
            Text("Seleccione su tipo de contribuyente:", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = userType == "natural",
                    onClick = { userType = "natural" }
                )
                Text(
                    "Persona Natural",
                    modifier = Modifier
                        .clickable { userType = "natural" }
                        .padding(start = 8.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = userType == "juridica",
                    onClick = { userType = "juridica" }
                )
                Text(
                    "Persona Jurídica",
                    modifier = Modifier
                        .clickable { userType = "juridica" }
                        .padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Entrada de ID
            OutlinedTextField(
                value = userIdInput,
                onValueChange = { userIdInput = it },
                label = { Text("Últimos dígitos de su ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de guardar
            Button(
                onClick = {
                    if (userType.isNotEmpty() && userIdInput.isNotEmpty()) {
                        if (userIdInput.matches(Regex("^\\d+$"))) {
                            viewModel.setReminders(userType, userIdInput)
                        } else {
                            Toast.makeText(
                                context,
                                "El ID debe contener solo números",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = userType.isNotEmpty() && userIdInput.isNotEmpty()
            ) {
                Text("Guardar configuración")
            }

            val showConfirmation by viewModel.showConfirmation.observeAsState(false)

            if (showConfirmation) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissConfirmation() },
                    title = { Text("Configuración guardada") },
                    text = {
                        Text(
                            "Recordatorios programados correctamente. " +
                                    "Se han programado ${viewModel.scheduledRemindersCount} recordatorios."
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.dismissConfirmation()
                                navController.popBackStack()
                            }
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}