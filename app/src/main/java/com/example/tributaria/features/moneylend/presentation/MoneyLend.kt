package com.example.tributaria.features.moneylend.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tributaria.BottomNavigationBar
import com.example.tributaria.CommonTopBar
import com.example.tributaria.CustomDrawer
import com.example.tributaria.headers.HeaderContentMoneyLend
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.tributaria.features.moneylend.repository.LoanRepository
import com.example.tributaria.features.moneylend.viewmodel.LoanViewModel


@Composable
fun MoneyLendScreen(navController: NavHostController, viewModel: LoanViewModel = viewModel(factory = LoanViewModelFactory(LoanRepository()))) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
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
                    HeaderContentMoneyLend(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Inputs para los valores
                Spacer(modifier = Modifier.height(16.dp))

                Text("Valor del crédito", fontSize = 16.sp)
                TextField(
                    value = uiState.loanAmount,
                    onValueChange = { viewModel.onLoanAmountChanged(it) },
                    label = { Text("Ingrese el valor del crédito") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tasa de interés mensual", fontSize = 16.sp)
                TextField(
                    value = uiState.interestRate,
                    onValueChange = { viewModel.onInterestRateChanged(it) },
                    label = { Text("Ingrese la tasa de interés mensual (ej. 1.2)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Plazo en meses", fontSize = 16.sp)
                TextField(
                    value = uiState.months,
                    onValueChange = { viewModel.onMonthsChanged(it) },
                    label = { Text("Ingrese el plazo en meses") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                uiState.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.calculateLoan() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.errorMessage == null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF))
                ) {
                    Text("Calcular intereses", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resultado del cálculo de intereses
                uiState.calculationResult?.let { result ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Total de intereses: $${"%,.2f".format(result.totalInterest)}",
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
                                    "Total de intereses pagados: $${"%,.2f".format(result.totalInterest)} a una tasa del ${uiState.interestRate}% mensual",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Justify
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Cuota mensual fija: $${"%,.2f".format(result.monthlyPayment)}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Justify
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Plazo: ${uiState.months} meses | Monto inicial: $${uiState.loanAmount}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Justify
                                )
                            }
                        }
                    }
                } ?: Text("Ingrese todos los valores para calcular los intereses del préstamo", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}

class LoanViewModelFactory(private val repository: LoanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}