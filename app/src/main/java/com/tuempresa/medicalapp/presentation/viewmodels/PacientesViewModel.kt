package com.tuempresa.medicalapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.repositories.PacienteRepository
import com.tuempresa.medicalapp.data.models.Consulta
import com.tuempresa.medicalapp.data.repositories.ConsultaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PacientesViewModel : ViewModel() {
    private val repo = PacienteRepository()
    private val consultaRepo = ConsultaRepository()
    private val _pacientes = MutableStateFlow<List<Paciente>>(emptyList())
    val pacientes: StateFlow<List<Paciente>> = _pacientes

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    val consultas: StateFlow<List<Consulta>> = _consultas

    fun cargarPacientes() {
        viewModelScope.launch {
            _pacientes.value = repo.obtenerPacientes()
            _consultas.value = consultaRepo.obtenerTodasLasConsultas()
        }
    }

    fun cargarPacientesPorDoctor(doctorId: String) {
        viewModelScope.launch {
            _pacientes.value = repo.obtenerPacientesPorDoctor(doctorId)
            _consultas.value = consultaRepo.obtenerTodasLasConsultas()
        }
    }

    fun agregarPaciente(paciente: Paciente) {
        viewModelScope.launch {
            repo.agregarPaciente(paciente)
            cargarPacientes()
        }
    }

    fun actualizarPaciente(paciente: Paciente) {
        viewModelScope.launch {
            repo.actualizarPaciente(paciente)
            cargarPacientes()
        }
    }

    fun eliminarPaciente(pacienteId: String) {
        viewModelScope.launch {
            repo.eliminarPaciente(pacienteId)
            cargarPacientes()
        }
    }

    fun darBajaPaciente(pacienteId: String) {
        viewModelScope.launch {
            repo.darBajaPaciente(pacienteId)
            cargarPacientes()
        }
    }

    fun reactivarPaciente(pacienteId: String) {
        viewModelScope.launch {
            repo.reactivarPaciente(pacienteId)
            cargarPacientes()
        }
    }

    // Método para cargar todos los pacientes (activos e inactivos) - para administración
    fun cargarTodosLosPacientes() {
        viewModelScope.launch {
            _pacientes.value = repo.obtenerTodosLosPacientes()
            _consultas.value = consultaRepo.obtenerTodasLasConsultas()
        }
    }
} 