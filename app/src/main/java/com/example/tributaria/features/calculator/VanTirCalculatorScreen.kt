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
                onLogout = { /* Logout logic */ }
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

                Text("Inversión inicial:", fontSize = 18.sp)
                OutlinedTextField(
                    value = inversionInicial,
                    onValueChange = { inversionInicial = it },
                    label = { Text("Inversión inicial") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF1E40AF),
                        unfocusedIndicatorColor = Color(0xFF1E40AF),
                        focusedLabelColor = Color(0xFF1E40AF),
                        cursorColor = Color(0xFF1E40AF)
                    )
                )

                Spacer(Modifier.height(8.dp))

                Text("Tipo de interés:", fontSize = 18.sp)
                OutlinedTextField(
                    value = tasaDescuento,
                    onValueChange = { tasaDescuento = it },
                    label = { Text("Tasa de descuento") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF1E40AF),
                        unfocusedIndicatorColor = Color(0xFF1E40AF),
                        focusedLabelColor = Color(0xFF1E40AF),
                        cursorColor = Color(0xFF1E40AF)
                    )
                )

                Spacer(Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Flujos de caja:", style = MaterialTheme.typography.titleMedium)

                    flujos.forEachIndexed { index, flujo ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            OutlinedTextField(
                                value = flujo,
                                onValueChange = { newValue ->
                                    flujos = flujos.toMutableList().also { it[index] = newValue }
                                },
                                label = { Text("Año ${index + 1}") },
                                placeholder = { Text("Flujo de caja (cobros - pagos)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color(0xFF1E40AF),
                                    unfocusedIndicatorColor = Color(0xFF1E40AF),
                                    focusedLabelColor = Color(0xFF1E40AF),
                                    cursorColor = Color(0xFF1E40AF)
                                )
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    if (flujos.size > 1) {
                                        flujos = flujos.toMutableList().also { it.removeAt(index) }
                                    } }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            flujos = (flujos + "") as MutableList<String>},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E40AF),
                            contentColor = Color.White
                        )

                    ) {
                        Text("Añadir año")
                    }
                }


                Spacer(Modifier.height(8.dp))

                // VAN
                OutlinedTextField(
                    value = van ?: "",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("VAN") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF1E40AF),
                        unfocusedIndicatorColor = Color(0xFF1E40AF),
                        focusedLabelColor = Color(0xFF1E40AF),
                        cursorColor = Color(0xFF1E40AF)
                    )
                )

                Spacer(Modifier.height(8.dp))

                // TIR
                OutlinedTextField(
                    value = tir ?: "",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("TIR") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF1E40AF),
                        unfocusedIndicatorColor = Color(0xFF1E40AF),
                        focusedLabelColor = Color(0xFF1E40AF),
                        cursorColor = Color(0xFF1E40AF)
                    )
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        calcularVanYTir(flujos, inversionInicial, tasaDescuento) { vanResult, tirResult ->
                            van = vanResult
                            tir = tirResult
                            feedbackMessage = generarRetroalimentacion(vanResult, tirResult, tasaDescuento)
                            showDialog = true

                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E40AF),
                        contentColor = Color.White
                    )
                ) {
                    Text("Calcular")
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Resultado", textAlign = TextAlign.Center)
                            }
                        },
                        text = {
                            Text(
                                text = feedbackMessage,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        confirmButton = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { showDialog = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1E40AF),
                                        contentColor = Color.White
                                    )
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
//Cálculos
fun calcularVanYTir(
    flujos: List<String>,
    inversionInicial: String,
    tasaDescuento: String,
    onResultado: (van: String, tir: String?) -> Unit
) {
    val flujoNumerico = flujos.mapNotNull { it.toDoubleOrNull() }
    val inversion = inversionInicial.toDoubleOrNull() ?: return
    val tasa = tasaDescuento.toDoubleOrNull()?.div(100) ?: return

    // VAN
    val vanCalculado = flujoNumerico
        .mapIndexed { i, f -> f / Math.pow(1 + tasa, (i + 1).toDouble()) }
        .sum() - inversion

    // TIR usando búsqueda binaria
    var low = -0.9999  // Tasa mínima permitida (puede ser negativa)
    var high = 1.0     // Tasa máxima permitida
    var tir = 0.0
    val epsilon = 1e-6  // Precisión deseada

    repeat(100) {
        val mid = (low + high) / 2
        val vanMid = flujoNumerico
            .mapIndexed { index, flujo -> flujo / Math.pow(1 + mid, (index + 1).toDouble()) }
            .sum() - inversion

        if (abs(vanMid) < epsilon) {
            tir = mid
            return@repeat
        }

        if (vanMid > 0) {
            low = mid
        } else {
            high = mid
        }
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
        van > 0 -> "✅ El VAN es mayor a 0, lo que indica que el proyecto es rentable."
        van == 0.0 -> "⚠️ El VAN es igual a 0, el proyecto es indiferente (sin ganancia ni pérdida)."
        else -> "❌ El VAN es menor a 0, el proyecto no es rentable."
    }

    val tirFeedback = when {
        tir > tmar && tir <= 25 -> "✅ La TIR es mayor a la tasa de descuento (TMAR), el proyecto es rentable."
        tir > tmar && tir > 25 -> "✅ La TIR es mayor a la TMAR, pero al ser superior al 25%, se recomienda revisar los flujos de caja por posible inconsistencia."
        tir == tmar -> "⚠️ La TIR es igual a la TMAR, el proyecto es indiferente."
        else -> "❌ La TIR es menor a la TMAR, el proyecto no es rentable."
    }

    return "$vanFeedback\n$tirFeedback"
}