package com.example.tributaria.features.calendar.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tributaria.BottomNavigationBar
import com.example.tributaria.CommonTopBar
import com.example.tributaria.CustomDrawer
import com.example.tributaria.features.calendar.viewmodel.CalendarViewModel
import com.example.tributaria.headers.HeaderContentCalendar
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val viewModel: CalendarViewModel = hiltViewModel()

    val scheduledReminders by viewModel.scheduledReminders.observeAsState(emptyList())
    val showConfirmation by viewModel.showConfirmation.observeAsState(false)
    val showError by viewModel.showError.observeAsState(false)




    LaunchedEffect(Unit) {
        viewModel.loadUserConfig()
        viewModel.loadAllReminders() // Cambiado a cargar todos los recordatorios
    }

    // Manejo de diálogos
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmation() },
            title = { Text("Recordatorios actualizados") },
            text = { Text("Tus recordatorios se han programado correctamente") },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissConfirmation() }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Error") },
            text = { Text("Ocurrió un error al gestionar los recordatorios") },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissError() }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawer(navController = navController, onLogout = { /* Logout logic */ })
        }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar {
                    HeaderContentCalendar(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
                    .verticalScroll(scrollState)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "¿Debes declarar renta este año?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navController.navigate("rentDeclarationCheck") },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Verificar ahora")
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val pdfUrl =
                                "https://www.dian.gov.co/Calendarios/Calendario_Tributario_2025.pdf"
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                                    .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario")
                            Text("Calendario tributario")
                        }
                    }

                    Button(
                        onClick = { navController.navigate("ReminderConfig") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Recordatorios")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Todos tus recordatorios programados:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                when {
                    scheduledReminders.isEmpty() -> {
                        Text(
                            text = "No tienes recordatorios programados",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            scheduledReminders.sortedBy { it.date }.forEach { reminder ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = reminder.message,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "Programado para: ${reminder.date}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.cancelReminder(
                                                    reminder.id,
                                                    reminder.workId
                                                )
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar recordatorio"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}