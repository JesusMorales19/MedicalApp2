package com.tuempresa.medicalapp.presentation.screens.doctor

import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.tuempresa.medicalapp.presentation.navigation.Navegacion.BottomNavigationBar
import com.tuempresa.medicalapp.presentation.navigation.Navegacion.PantallaActual
import com.tuempresa.medicalapp.presentation.screens.doctor.PacientesScreen
import com.tuempresa.medicalapp.presentation.screens.doctor.PerfilDoctorScreen
import com.tuempresa.medicalapp.presentation.screens.doctor.DetallePacienteScreen
import com.tuempresa.medicalapp.presentation.screens.common.ServiciosMedicos
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import com.tuempresa.medicalapp.presentation.screens.doctor.SurgeryHistoryScreen
import com.tuempresa.medicalapp.presentation.screens.doctor.HistorialConsultasScreen
import com.tuempresa.medicalapp.data.models.Paciente
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuempresa.medicalapp.presentation.viewmodels.PacientesViewModel
import com.tuempresa.medicalapp.presentation.viewmodels.CirugiasViewModel
import com.tuempresa.medicalapp.presentation.viewmodels.RadiografiasViewModel
import androidx.compose.ui.platform.LocalContext
import com.tuempresa.medicalapp.core.utils.ProyeccionUtils


@Composable
fun BottomNavigationBar(
    pantallaActual: PantallaActual,
    onPantallaSeleccionada: (PantallaActual) -> Unit
) {
    val blue = Color(0xFF183A6D)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(Color(0xFFF5F7FA)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPantallaSeleccionada(PantallaActual.PACIENTES) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Pacientes",
                tint = if (pantallaActual == PantallaActual.PACIENTES) blue else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(
            onClick = { onPantallaSeleccionada(PantallaActual.PERFIL) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configuración",
                tint = if (pantallaActual == PantallaActual.PERFIL) blue else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun MainScreen(
    authViewModel: com.tuempresa.medicalapp.presentation.viewmodels.AuthViewModel? = null,
    onBackPressed: ((() -> Boolean)?) -> Unit = {}
) {
    var pantallaActual by remember { mutableStateOf(PantallaActual.PACIENTES) }
    var selectedPaciente by remember { mutableStateOf<Paciente?>(null) }
    var showDetalle by remember { mutableStateOf(false) }
    var showServiciosMedicos by remember { mutableStateOf(false) }
    var showHistorialRadiografias by remember {mutableStateOf(false)}
    var showHistorialCirugias by remember { mutableStateOf(false) }
    var showHistorialConsultas by remember { mutableStateOf(false) }

    val pacientesViewModel: PacientesViewModel = viewModel()
    val cirugiasViewModel: CirugiasViewModel = viewModel()
    val radiografiasViewModel: RadiografiasViewModel = viewModel()
    val consultas by pacientesViewModel.consultas.collectAsState()
    val cirugias by cirugiasViewModel.cirugias.collectAsState(initial = emptyList())
    val radiografias by radiografiasViewModel.radiografias.collectAsState(initial = emptyList())

    // Función para manejar el botón de regreso
    val handleBackPress = {
        when {
            showHistorialConsultas -> {
                showHistorialConsultas = false
                showServiciosMedicos = true
                true
            }
            showHistorialCirugias -> {
                showHistorialCirugias = false
                showServiciosMedicos = true
                true
            }
            showHistorialRadiografias -> {
                showHistorialRadiografias = false
                showServiciosMedicos = true
                true
            }
            showServiciosMedicos -> {
                showServiciosMedicos = false
                showDetalle = true
                true
            }
            showDetalle -> {
                showDetalle = false
                pantallaActual = PantallaActual.PACIENTES
                true
            }
            pantallaActual == PantallaActual.PERFIL -> {
                pantallaActual = PantallaActual.PACIENTES
                true
            }
            else -> false // No hay más pantallas para regresar, cerrar la app
        }
    }

    // Configurar el callback de regreso
    LaunchedEffect(Unit) {
        onBackPressed(handleBackPress)
    }

    // Actualizar el callback cuando cambien las variables de navegación
    LaunchedEffect(showDetalle, showServiciosMedicos, showHistorialRadiografias, showHistorialCirugias, showHistorialConsultas, pantallaActual) {
        onBackPressed(handleBackPress)
    }

    Scaffold(
        bottomBar = {
            if (!showDetalle && !showServiciosMedicos && !showHistorialRadiografias && !showHistorialCirugias && !showHistorialConsultas) {
                BottomNavigationBar(
                    pantallaActual = pantallaActual,
                    onPantallaSeleccionada = { pantallaActual = it }
                )
            }
        }
    ) { padding ->
        when {
            showHistorialCirugias -> {
                val authUiState by authViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(null) }
                val doctor = authUiState?.doctor
                
                SurgeryHistoryScreen(
                    pacienteId = selectedPaciente?.id ?: "",
                    paciente = selectedPaciente,
                    doctor = doctor,
                    onBack = {
                        showHistorialCirugias = false
                        showServiciosMedicos = true
                    }
                )
            }
            showHistorialRadiografias -> {
                val paciente = selectedPaciente
                val context = LocalContext.current
                
                // Cargar radiografías del paciente cuando se muestra la pantalla
                LaunchedEffect(paciente?.id) {
                    if (paciente?.id != null) {
                        radiografiasViewModel.cargarRadiografiasPorPaciente(paciente.id)
                    }
                }
                
                val radiografiasPaciente = radiografias.filter { it.pacienteId == paciente?.id }
                
                HistorialRadiografia(
                    nombre = selectedPaciente?.nombre ?: "",
                    onBack = {
                        showHistorialRadiografias = false
                        showServiciosMedicos = true
                    },
                    onToraxClick = {
                        if (paciente != null) {
                            ProyeccionUtils.enviarRadiografiasPorTipo(
                                context = context,
                                paciente = paciente,
                                radiografias = radiografiasPaciente,
                                tipoRadiografia = "Tórax"
                            )
                        }
                    },
                    onExtremidadesClick = {
                        if (paciente != null) {
                            ProyeccionUtils.enviarRadiografiasPorTipo(
                                context = context,
                                paciente = paciente,
                                radiografias = radiografiasPaciente,
                                tipoRadiografia = "Extremidades"
                            )
                        }
                    },
                    onColumnaClick = {
                        if (paciente != null) {
                            ProyeccionUtils.enviarRadiografiasPorTipo(
                                context = context,
                                paciente = paciente,
                                radiografias = radiografiasPaciente,
                                tipoRadiografia = "Columna"
                            )
                        }
                    }
                )
            }
            showHistorialConsultas -> {
                println("DEBUG: MainScreen - Enviando pacienteId: ${selectedPaciente?.id}")
                val authUiState by authViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(null) }
                val doctor = authUiState?.doctor
                
                HistorialConsultasScreen(
                    pacienteId = selectedPaciente?.id ?: "",
                    paciente = selectedPaciente,
                    doctor = doctor,
                    onBack = {
                        showHistorialConsultas = false
                        showServiciosMedicos = true
                    }
                )
            }
            showServiciosMedicos -> {
                ServiciosMedicos(
                    nombre = selectedPaciente?.nombre ?: "",
                    onBack = {
                        showServiciosMedicos = false
                        showDetalle = true
                    },
                    onRadiografiaClick = {
                        showServiciosMedicos = false
                        showHistorialRadiografias = true
                    },
                    onCirugiasClick = {
                        showServiciosMedicos = false
                        showHistorialCirugias = true
                    },
                    onUltimasConsultasClick = {
                        showServiciosMedicos = false
                        showHistorialConsultas = true
                    }
                )
            }
            showDetalle -> {
                val paciente = selectedPaciente
                val consultasPaciente = consultas.filter { it.pacienteId == paciente?.id }
                val cirugiasPaciente = cirugias.filter { it.pacienteId == paciente?.id }
                val radiografiasPaciente = radiografias.filter { it.pacienteId == paciente?.id }
                val authUiState by authViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(null) }
                
                DetallePacienteScreen(
                    paciente = paciente,
                    consultas = consultasPaciente,
                    cirugias = cirugiasPaciente,
                    radiografias = radiografiasPaciente,
                    nombreDoctor = authUiState?.doctor?.nombre ?: "",
                    apellidoDoctor = authUiState?.doctor?.apellido ?: "",
                    fotoDoctor = authUiState?.doctor?.fotoBase64 ?: "",
                    onBack = {
                        showDetalle = false
                        pantallaActual = PantallaActual.PACIENTES
                    },
                    onServiciosClick = {
                        showDetalle = false
                        showServiciosMedicos = true
                    },
                    onHistorialClick = { /* Navegar a historial */ }
                )
            }
            pantallaActual == PantallaActual.PACIENTES -> {
                PacientesScreen(
                    modifier = Modifier.padding(padding),
                    onPacienteClick = { paciente ->
                        selectedPaciente = paciente
                        showDetalle = true
                    },
                    authViewModel = authViewModel
                )
            }
            pantallaActual == PantallaActual.PERFIL -> {
                PerfilDoctorScreen()
            }
        }
    }
} 