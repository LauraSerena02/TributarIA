package com.example.tributaria.features.balancepoint.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tributaria.features.balancepoint.repository.BalancePointRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class BalancePointViewModel(
    private val repository: BalancePointRepository = BalancePointRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BalancePointUiState())
    val uiState: StateFlow<BalancePointUiState> = _uiState


    private fun formatNumber(input: String): String {
        // Dejar solo dígitos
        val digits = input.filter { it.isDigit() }

        if (digits.isEmpty()) return ""

        val number = digits.toLong()
        val formatter = NumberFormat.getInstance(Locale("es", "CO")) as DecimalFormat
        formatter.applyPattern("#,###")

        return formatter.format(number)
    }

    /** 👉 INPUTS CON FORMATO **/
    fun onCostoVariableChanged(value: String) {
        _uiState.update { it.copy(costoVariable = formatNumber(value)) }
        validar()
    }

    fun onCostoFijoChanged(value: String) {
        _uiState.update { it.copy(costoFijo = formatNumber(value)) }
        validar()
    }

    fun onPrecioUnitarioChanged(value: String) {
        _uiState.update { it.copy(precioUnitario = formatNumber(value)) }
        validar()
    }


    fun onCalcularClick() {
        validar()

        if (_uiState.value.errorMessage != null) return

        val costoVariable = _uiState.value.costoVariable.replace(".", "").toFloat()
        val costoFijo = _uiState.value.costoFijo.replace(".", "").toFloat()
        val precioUnitario = _uiState.value.precioUnitario.replace(".", "").toFloat()

        val resultado = repository.calcularPuntoEquilibrio(costoVariable, costoFijo, precioUnitario)
        _uiState.update { it.copy(puntoEquilibrio = resultado) }
    }

    /** VALIDACIONES **/
    private fun validar() {
        val state = _uiState.value

        if (state.costoVariable.isBlank() ||
            state.costoFijo.isBlank() ||
            state.precioUnitario.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Ningún campo puede estar vacío.") }
            return
        }

        val costoVariable = state.costoVariable.replace(".", "").toFloatOrNull()
        val costoFijo = state.costoFijo.replace(".", "").toFloatOrNull()
        val precioUnitario = state.precioUnitario.replace(".", "").toFloatOrNull()

        if (costoVariable == null || costoFijo == null || precioUnitario == null) {
            _uiState.update { it.copy(errorMessage = "Todos los valores deben ser números válidos.") }
            return
        }

        if (costoVariable < 0 || costoFijo < 0 || precioUnitario < 0) {
            _uiState.update { it.copy(errorMessage = "Los valores no pueden ser negativos.") }
            return
        }

        if (costoVariable >= precioUnitario) {
            _uiState.update {
                it.copy(errorMessage = "El costo variable debe ser menor que el precio unitario.")
            }
            return
        }

        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class BalancePointUiState(
    val costoVariable: String = "",
    val costoFijo: String = "",
    val precioUnitario: String = "",
    val puntoEquilibrio: Float? = null,
    val errorMessage: String? = null
)
