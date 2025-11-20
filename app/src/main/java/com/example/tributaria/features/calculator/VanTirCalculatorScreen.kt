package com.example.tributaria.features.calculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tributaria.BottomNavigationBar
import com.example.tributaria.CommonTopBar
import com.example.tributaria.CustomDrawer
import com.example.tributaria.headers.HeaderCalculatorVanTir
import kotlinx.coroutines.launch
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.abs

@Composable
fun VanTirCalculatorScreen(navController: NavHostController) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var inversionInicial by remember { mutableStateOf("") }
    var tasaDescuento by remember { mutableStateOf("") }
    var flujos by remember { mutableStateOf(mutableListOf("")) }

    var van by remember { mutableStateOf<String?>(null) }
    var tir by remember { mutableStateOf<String?>(null) }

    var feedbackMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawer(
                navController = navController,
                onLogout = { }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar {
                    HeaderCalculatorVanTir(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {

                Spacer(Modifier.height(16.dp))

                // -----------------------------
                // INVERSIÓN INICIAL
                // -----------------------------
                Text("Inversión inicial:", fontSize = 18.sp)
                OutlinedTextField(
                    value = inversionInicial,
                    onValueChange = { inversionInicial = it },
                    label = { Text("Inversión inicial") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(Modifier.height(8.dp))

                // -----------------------------
                // TASA DE DESCUENTO
                // -----------------------------
                Text("Tasa de descuento (%):", fontSize = 18.sp)
                OutlinedTextField(
                    value = tasaDescuento,
                    onValueChange = { tasaDescuento = it },
                    label = { Text("Tasa de descuento") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(Modifier.height(16.dp))

                // -----------------------------
                // FLUJOS DE CAJA
                // -----------------------------
                Column(modifier = Modifier.fillMaxWidth()) {

                    Text("Flujos de caja:", style = MaterialTheme.typography.titleMedium)

                    flujos.forEachIndexed { index, flujo ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {

                            OutlinedTextField(
                                value = flujo,
                                onValueChange = { newValue ->
                                    flujos = flujos.toMutableList().also { it[index] = newValue }
                                },
                                label = { Text("Año ${index + 1}") },
                                placeholder = { Text("Flujo (cobros - pagos)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                colors = textFieldColors()
                            )

                            IconButton(
                                onClick = {
                                    if (flujos.size > 1) {
                                        flujos = flujos.toMutableList().also { it.removeAt(index) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            flujos = (flujos + "") as MutableList<String>
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = buttonColorsBlue()
                    ) {
                        Text("Añadir año")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // -----------------------------
                // RESULTADOS VAN Y TIR
                // -----------------------------
                OutlinedTextField(
                    value = van ?: "",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("VAN") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = tir ?: "",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("TIR (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(Modifier.height(12.dp))

                // -----------------------------
                // BOTÓN CALCULAR + VALIDACIONES
                // -----------------------------
                Button(
                    onClick = {

                        // ====== VALIDACIONES ======

                        // Inversión inicial
                        if (inversionInicial.isBlank()) {
                            feedbackMessage = "Debes ingresar la inversión inicial."
                            showDialog = true
                            return@Button
                        }
                        if (inversionInicial.toDoubleOrNull() == null) {
                            feedbackMessage = "La inversión inicial debe ser un número válido."
                            showDialog = true
                            return@Button
                        }
                        if (inversionInicial.toDouble() <= 0) {
                            feedbackMessage = "La inversión inicial debe ser mayor que cero."
                            showDialog = true
                            return@Button
                        }

                        // Tasa de descuento
                        if (tasaDescuento.isBlank()) {
                            feedbackMessage = "Debes ingresar la tasa de descuento."
                            showDialog = true
                            return@Button
                        }
                        if (tasaDescuento.toDoubleOrNull() == null) {
                            feedbackMessage = "La tasa de descuento debe ser un número válido."
                            showDialog = true
                            return@Button
                        }
                        if (tasaDescuento.toDouble() <= 0) {
                            feedbackMessage = "La tasa de descuento debe ser mayor a 0%."
                            showDialog = true
                            return@Button
                        }

                        // Flujos de caja
                        if (flujos.isEmpty()) {
                            feedbackMessage = "Debes ingresar al menos un flujo de caja."
                            showDialog = true
                            return@Button
                        }

                        flujos.forEachIndexed { index, flujo ->
                            if (flujo.isBlank()) {
                                feedbackMessage = "El flujo del año ${index + 1} no puede estar vacío."
                                showDialog = true
                                return@Button
                            }
                            if (flujo.toDoubleOrNull() == null) {
                                feedbackMessage = "El flujo del año ${index + 1} debe ser un número válido."
                                showDialog = true
                                return@Button
                            }
                        }

                        // -----------------------------
                        // SI TODO ES VÁLIDO → CALCULAR
                        // -----------------------------
                        calcularVanYTir(flujos, inversionInicial, tasaDescuento) { vanResult, tirResult ->
                            van = vanResult
                            tir = tirResult
                            feedbackMessage = generarRetroalimentacion(vanResult, tirResult, tasaDescuento)
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = buttonColorsBlue()
                ) {
                    Text("Calcular")
                }

                // -----------------------------
                // ALERT DIALOG (Resultados / Errores)
                // -----------------------------
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Resultado", textAlign = TextAlign.Center)
                            }
                        },
                        text = {
                            Text(
                                text = feedbackMessage,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        confirmButton = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { showDialog = false },
                                    colors = buttonColorsBlue()
                                ) {
                                    Text("Aceptar")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedIndicatorColor = Color(0xFF1E40AF),
    unfocusedIndicatorColor = Color(0xFF1E40AF),
    focusedLabelColor = Color(0xFF1E40AF),
    cursorColor = Color(0xFF1E40AF)
)

@Composable
private fun buttonColorsBlue() = ButtonDefaults.buttonColors(
    containerColor = Color(0xFF1E40AF),
    contentColor = Color.White
)

// -----------------------------
// CÁLCULO DE VAN Y TIR
// -----------------------------
fun calcularVanYTir(
    flujos: List<String>,
    inversionInicial: String,
    tasaDescuento: String,
    onResultado: (van: String, tir: String?) -> Unit
) {

    val flujoNumerico = flujos.mapNotNull { it.toDoubleOrNull() }
    val inversion = inversionInicial.toDoubleOrNull() ?: return
    val tasa = tasaDescuento.toDoubleOrNull()?.div(100) ?: return

    val vanCalculado = flujoNumerico
        .mapIndexed { i, f -> f / Math.pow(1 + tasa, (i + 1).toDouble()) }
        .sum() - inversion

    // TIR
    var low = -0.9999
    var high = 1.0
    var tir = 0.0
    val epsilon = 1e-6

    repeat(100) {
        val mid = (low + high) / 2
        val vanMid = flujoNumerico
            .mapIndexed { index, flujo -> flujo / Math.pow(1 + mid, (index + 1).toDouble()) }
            .sum() - inversion

        if (abs(vanMid) < epsilon) {
            tir = mid
            return@repeat
        }

        if (vanMid > 0) low = mid else high = mid
    }

    val tirCalculada = tir * 100

    onResultado(
        "%.2f".format(vanCalculado),
        "%.3f".format(tirCalculada)
    )
}

fun generarRetroalimentacion(vanStr: String, tirStr: String?, tasaDescuentoStr: String): String {

    val van = vanStr.toDoubleOrNull() ?: return ""
    val tir = tirStr?.toDoubleOrNull() ?: return ""
    val tmar = tasaDescuentoStr.toDoubleOrNull() ?: return ""

    val vanFeedback = when {
        van > 0 -> "El VAN es mayor a 0, el proyecto es rentable."
        van == 0.0 -> "El VAN es igual a 0, el proyecto es indiferente."
        else -> "El VAN es menor a 0, el proyecto no es rentable."
    }

    val tirFeedback = when {
        tir > tmar && tir <= 25 -> "La TIR supera la TMAR, el proyecto es rentable."
        tir > tmar && tir > 25 -> "La TIR es muy alta (>25%), revisar los flujos por posible inconsistencia."
        tir == tmar -> "La TIR es igual a la TMAR, el proyecto no genera valor adicional."
        else -> "La TIR es menor que la TMAR, el proyecto no es rentable."
    }

    return "$vanFeedback\n$tirFeedback"
}
