package com.tuempresa.medicalapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.tuempresa.medicalapp.data.models.Radiografia
import com.tuempresa.medicalapp.data.repositories.RadiografiaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RadiografiasViewModel : ViewModel() {
    private val _radiografias = MutableStateFlow<List<Radiografia>>(emptyList())
    val radiografias: StateFlow<List<Radiografia>> = _radiografias
    
    private val repository = RadiografiaRepository()

    fun cargarRadiografiasPorPaciente(pacienteId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val radiografias = repository.obtenerRadiografiasPorPaciente(pacienteId)
                _radiografias.value = radiografias
            } catch (e: Exception) {
                // Manejar error si es necesario
                _radiografias.value = emptyList()
            }
        }
    }
} 