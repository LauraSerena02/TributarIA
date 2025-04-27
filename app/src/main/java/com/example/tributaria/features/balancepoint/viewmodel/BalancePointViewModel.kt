package com.example.tributaria.features.balancepoint.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tributaria.features.balancepoint.repository.BalancePointRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BalancePointViewModel(
    private val repository: BalancePointRepository = BalancePointRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BalancePointUiState())
    val uiState: StateFlow<BalancePointUiState> = _uiState

    fun onCostoVariableChanged(value: String) {
        _uiState.update { it.copy(costoVariable = value) }
        validar()
    }

    fun onCostoFijoChanged(value: String) {
        _uiState.update { it.copy(costoFijo = value) }
    }

    fun onPrecioUnitarioChanged(value: String) {
        _uiState.update { it.copy(precioUnitario = value) }
        validar()
    }

    fun onCalcularClick() {
        val costoVariable = _uiState.value.costoVariable.toFloatOrNull() ?: 0f
        val costoFijo = _uiState.value.costoFijo.toFloatOrNull() ?: 0f
        val precioUnitario = _uiState.value.precioUnitario.toFloatOrNull() ?: 0f

        val resultado = repository.calcularPuntoEquilibrio(costoVariable, costoFijo, precioUnitario)
        _uiState.update { it.copy(puntoEquilibrio = resultado) }
    }

    private fun validar() {
        val costoVariable = _uiState.value.costoVariable.toFloatOrNull()
        val precioUnitario = _uiState.value.precioUnitario.toFloatOrNull()

        if (costoVariable != null && precioUnitario != null) {
            if (costoVariable >= precioUnitario) {
                _uiState.update { it.copy(errorMessage = "El costo variable no puede ser mayor o igual al precio unitario.") }
            } else {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }
}
data class BalancePointUiState(
    val costoVariable: String = "",
    val costoFijo: String = "",
    val precioUnitario: String = "",
    val puntoEquilibrio: Float? = null,
    val errorMessage: String? = null
)