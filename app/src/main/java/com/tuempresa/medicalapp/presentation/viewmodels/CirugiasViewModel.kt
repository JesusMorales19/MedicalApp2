package com.tuempresa.medicalapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.medicalapp.data.models.Cirugia
import com.tuempresa.medicalapp.data.repositories.CirugiaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

data class CirugiasUiState(
    val isLoading: Boolean = false,
    val cirugias: List<Cirugia> = emptyList(),
    val error: String? = null
)

class CirugiasViewModel : ViewModel() {
    private val repository = CirugiaRepository()
    
    private val _uiState = MutableStateFlow(CirugiasUiState())
    val uiState: StateFlow<CirugiasUiState> = _uiState.asStateFlow()

    // Propiedad pública para acceder directamente a la lista de cirugías
    val cirugias: StateFlow<List<Cirugia>> get() = _uiState.asStateFlow().map { it.cirugias }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    fun cargarCirugiasPorPaciente(pacienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val cirugias = repository.obtenerCirugiasPorPaciente(pacienteId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    cirugias = cirugias
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar cirugías: ${e.message}"
                )
            }
        }
    }

    fun cargarTodasLasCirugias() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val cirugias = repository.obtenerTodasLasCirugias()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    cirugias = cirugias
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar cirugías: ${e.message}"
                )
            }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
} 