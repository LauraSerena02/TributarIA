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
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment

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
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color(0xFF1E40AF),
                                    unfocusedIndicatorColor = Color(0xFF1E40AF),
                                    focusedLabelColor = Color(0xFF1E40AF),
                                    cursorColor = Color(0xFF1E40AF)
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                if (flujos.size > 1) {
                                    flujos = flujos.toMutableList().also { it.removeAt(index) }
                                } }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.Red)
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
            }
        }
    }
}

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
    val tirEstimado = try {
        val tasaEstimada = (flujoNumerico.sum() - inversion) / inversion
        "%.2f".format(tasaEstimada * 100)
    } catch (e: Exception) {
        null
    }
    onResultado("%.2f".format(vanCalculado), tirEstimado)
}