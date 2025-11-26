// BalancePointScreen.kt
package com.example.tributaria.features.balancepoint.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tributaria.BottomNavigationBar
import com.example.tributaria.CommonTopBar
import com.example.tributaria.CustomDrawer
import com.example.tributaria.features.balancepoint.viewmodel.BalancePointViewModel
import com.example.tributaria.headers.HeaderContentBalancePoint
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding




@Composable
fun BalancePointScreen(navController: NavHostController, viewModel: BalancePointViewModel = viewModel()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Obtener el estado de UI del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawer(
                navController = navController,
                onLogout = {}
            )
        }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar {
                    HeaderContentBalancePoint(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())  // Permite hacer scroll
            ) {
                // Inputs para los valores
                Spacer(modifier = Modifier.height(16.dp))

                Text("Costo Variable Unitario", fontSize = 16.sp)
                TextField(
                    value = uiState.costoVariable,
                    onValueChange = { viewModel.onCostoVariableChanged(it) },
                    label = { Text("Ingrese el costo variable") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Costo Fijo Total", fontSize = 16.sp)
                TextField(
                    value = uiState.costoFijo,
                    onValueChange = { viewModel.onCostoFijoChanged(it) },
                    label = { Text("Ingrese el costo fijo") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Precio de Venta Unitario", fontSize = 16.sp)
                TextField(
                    value = uiState.precioUnitario,
                    onValueChange = { viewModel.onPrecioUnitarioChanged(it) },
                    label = { Text("Ingrese el precio de venta unitario") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                uiState.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.onCalcularClick() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.errorMessage == null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF))
                ) {
                    Text("Calcular Punto de Equilibrio", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                uiState.puntoEquilibrio?.let { punto ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Punto de Equilibrio: ${"%.2f".format(punto)} unidades",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E40AF).copy(alpha = 0.8f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Para que tus ingresos igualen a tus costos, debes vender ${"%.2f".format(punto)} unidades.",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Justify
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Si vendes más de ${"%.2f".format(punto)} unidades, obtendrás ganancias. Si vendes menos, tendrás pérdidas.",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Justify
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Recuerda que los costos fijos (anuales, mensuales, trimestrales, semestrales) corresponden  a las unidades a vender o producir en ese periodo para encontrar el punto de equilibrio.",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Justify
                                )
                            }
                        }

                        // Aquí es donde llamamos al gráfico y nos aseguramos de que sea visible
                        BalancePointGraph(
                            puntoEquilibrio = punto,
                            costoFijo = uiState.costoFijo.toFloat(),
                            costoVariable = uiState.costoVariable.toFloat(),
                            precioUnitario = uiState.precioUnitario.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(16.dp)
                        )
                    }
                } ?: Text("Ingrese todos los valores para calcular el punto de equilibrio y generar el gráfico", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}
