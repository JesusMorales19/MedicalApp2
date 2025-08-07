package com.tuempresa.medicalapp.presentation.screens.secretary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuempresa.medicalapp.R
import com.tuempresa.medicalapp.core.utils.ImageUtils
import com.tuempresa.medicalapp.presentation.components.forms.AddDoctorForm
import com.tuempresa.medicalapp.presentation.components.forms.AddPacienteForm
import com.tuempresa.medicalapp.presentation.components.forms.EditPacienteForm
import com.tuempresa.medicalapp.presentation.components.RecetaDialog
import com.tuempresa.medicalapp.presentation.viewmodels.AuthViewModel
import com.tuempresa.medicalapp.presentation.viewmodels.PacientesViewModel
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.repositories.ConsultaRepository
import com.tuempresa.medicalapp.core.utils.RecetaGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Refresh
import com.tuempresa.medicalapp.data.repositories.RadiografiaRepository
import com.tuempresa.medicalapp.presentation.components.forms.AddCirugiaForm
import com.tuempresa.medicalapp.presentation.components.forms.AddRadiografiaForm
import com.tuempresa.medicalapp.presentation.navigation.Navegacion.BottomNavigationBar
import com.tuempresa.medicalapp.presentation.navigation.Navegacion.PantallaActual
import com.tuempresa.medicalapp.data.repositories.CirugiaRepository


@Composable
fun SecretariaScreen(
    pacientesViewModel: PacientesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onBackPressed: ((() -> Boolean)?) -> Unit = {}
) {
    var pantallaActual by remember { mutableStateOf(PantallaActual.PACIENTES) }
    var selectedTab by remember { mutableStateOf("pacientes") }
    var blue = Color(0xFF183A6D)

    // Función para manejar el botón de regreso
    val handleBackPress = {
        when (pantallaActual) {
            PantallaActual.PERFIL -> {
                pantallaActual = PantallaActual.PACIENTES
                true
            }
            PantallaActual.PACIENTES -> {
                false // No hay más pantallas para regresar, cerrar la app
            }
        }
    }

    // Configurar el callback de regreso
    LaunchedEffect(Unit) {
        onBackPressed(handleBackPress)
    }

    // Actualizar el callback cuando cambie la pantalla actual
    LaunchedEffect(pantallaActual) {
        onBackPressed(handleBackPress)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                pantallaActual = pantallaActual,
                onPantallaSeleccionada = { pantallaActual = it }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (pantallaActual == PantallaActual.PACIENTES) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp)
                        .padding(bottom = padding.calculateBottomPadding())
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { selectedTab = "pacientes" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == "pacientes") blue else Color.LightGray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Pacientes", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { selectedTab = "doctores" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == "doctores") blue else Color.LightGray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Doctores", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    when (selectedTab) {
                        "pacientes" -> {
                            PacientesSecretariaContent(viewModel = pacientesViewModel, authViewModel = authViewModel)
                        }
                        "doctores" -> {
                            DoctoresSecretariaContent(authViewModel = authViewModel)
                        }
                    }
                }
            } else if (pantallaActual == PantallaActual.PERFIL) {
                PerfilSecretariaScreen(authViewModel = authViewModel)
            }
        }
    }
}

// Formularios movidos a archivos separados:
// - AddUserDialog -> AddDoctorForm.kt
// - AddPacienteDialog -> AddPacienteForm.kt
// - EditarPacienteDialog -> EditPacienteForm.kt
// - AgregarConsultaDialog -> AddConsultaForm.kt
// - SubirRadiografiaDialog -> AddRadiografiaForm.kt
// - AgregarCirugiaDialog -> AddCirugiaForm.kt

@Composable
fun DropdownMenuBoxReal(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text("Selecciona") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun PacientesSecretariaContent(viewModel: PacientesViewModel, authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val pacientes by viewModel.pacientes.collectAsState()
    val listaDoctores = authViewModel.uiState.collectAsState().value.listaDoctores
    var showAddDialog by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var doctorSearch by remember { mutableStateOf("") }
    var pacienteEditando by remember { mutableStateOf<Paciente?>(null) }
    var pacienteAEliminar by remember { mutableStateOf<Paciente?>(null) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var isGeneratingReceta by remember { mutableStateOf(false) }
    var recetaGenerada by remember { mutableStateOf<File?>(null) }
    var showRecetaDialog by remember { mutableStateOf(false) }
    var pacienteReceta by remember { mutableStateOf<com.tuempresa.medicalapp.data.models.Paciente?>(null) }
    var ultimaConsultaReceta by remember { mutableStateOf<com.tuempresa.medicalapp.data.models.Consulta?>(null) }
    
    // Estados para baja lógica de pacientes
    var pacienteABajar by remember { mutableStateOf<Paciente?>(null) }
    var pacienteAReactivar by remember { mutableStateOf<Paciente?>(null) }
    var showConfirmBajaPacienteDialog by remember { mutableStateOf(false) }
    var showConfirmReactivarPacienteDialog by remember { mutableStateOf(false) }
    
    // Estados para radiografías
    var pacienteParaRadiografia by remember { mutableStateOf<Paciente?>(null) }
    
    // Estados para cirugías
    var pacienteParaCirugia by remember { mutableStateOf<Paciente?>(null) }
    var ultimaConsultaCirugia by remember { mutableStateOf<com.tuempresa.medicalapp.data.models.Consulta?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.cargarTodosLosPacientes() // Cargar todos los pacientes (activos e inactivos)
        authViewModel.cargarListaDoctores()
    }
    
    LaunchedEffect(listaDoctores) {
        android.util.Log.d("PACIENTES_SECRETARIA", "Doctores disponibles: ${listaDoctores.size}, Especialidades: ${listaDoctores.map { it.especialidad }.distinct()}")
    }
    
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Pacientes", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Button(
                onClick = {
                    authViewModel.cargarListaDoctores()
                    showAddDialog = true
                },
                enabled = listaDoctores.any { it.especialidad.isNotBlank() }
            ) {
                Icon(Icons.Default.Person, contentDescription = "Agregar")
                Spacer(Modifier.width(8.dp))
                Text("Agregar")
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Buscar paciente") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = doctorSearch,
                onValueChange = { doctorSearch = it },
                label = { Text("Buscar doctor") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        val pacientesFiltrados = pacientes.filter { paciente ->
            (search.isBlank() || (paciente.nombre + " " + paciente.apellido).contains(search, ignoreCase = true)) &&
            (doctorSearch.isBlank() || listaDoctores.find { it.id == paciente.idDoctorAsignado }?.let { (it.nombre + " " + it.apellido).contains(doctorSearch, ignoreCase = true) } == true)
        }
        LazyColumn {
            items(pacientesFiltrados) { paciente ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(if (paciente.activo) Color.White else Color(0xFFF5F5F5))
                ) {
                    if (paciente.fotoBase64.isNotBlank()) {
                        val bitmap = ImageUtils.decodeBase64ToBitmap(paciente.fotoBase64)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Foto paciente",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFF183A6D), CircleShape)
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            paciente.nombre + " " + paciente.apellido,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF183A6D)
                        )
                        if (!paciente.activo) {
                            Text(
                                "INACTIVO",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    // Botones de acción
                    Row {
                        if (paciente.activo) {
                            // Botón de editar
                            IconButton(onClick = { pacienteEditando = paciente }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF183A6D))
                            }
                            
                            // Botón de baja lógica
                            IconButton(
                                onClick = { 
                                    pacienteABajar = paciente
                                    showConfirmBajaPacienteDialog = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.DoNotDisturb,
                                    contentDescription = "Dar de baja",
                                    tint = Color.Blue
                                )
                            }
                            
                            // Botón de eliminar completamente
                            IconButton({ 
                                pacienteAEliminar = paciente
                                showConfirmDeleteDialog = true
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                            
                            // Botón de radiografía
                            IconButton(onClick = { pacienteParaRadiografia = paciente }) {
                                Icon(painterResource(id = R.drawable.ic_icon_radiografia), contentDescription = "Subir radiografía", tint = Color(0xFF183A6D))
                            }
                            
                            // Botón de cirugía
                            IconButton(onClick = { 
                                // Cargar la última consulta del paciente para cirugías
                                CoroutineScope(Dispatchers.IO).launch {
                                    val consultas = ConsultaRepository().obtenerConsultasPorPaciente(paciente.id)
                                    val ultimaConsultaOrdenada = consultas.sortedByDescending { 
                                        try {
                                            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                            java.time.LocalDate.parse(it.fecha, formatter)
                                        } catch (e: Exception) {
                                            java.time.LocalDate.MIN
                                        }
                                    }.firstOrNull()
                                    
                                    // Actualizar en el hilo principal
                                    ultimaConsultaCirugia = ultimaConsultaOrdenada
                                    pacienteParaCirugia = paciente
                                }
                            }) {
                                Icon(painterResource(id = R.drawable.ic_icon_cirujia), contentDescription = "Registrar cirugía", tint = Color(0xFF183A6D))
                            }
                            
                            // Botón de receta
                            IconButton(onClick = { 
                                // Generar receta del paciente
                                isGeneratingReceta = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val consultas = ConsultaRepository().obtenerConsultasPorPaciente(paciente.id)
                                        val ultimaConsultaReceta = consultas.sortedByDescending { 
                                            try {
                                                val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                                java.time.LocalDate.parse(it.fecha, formatter)
                                            } catch (e: Exception) {
                                                java.time.LocalDate.MIN
                                            }
                                        }.firstOrNull()
                                        
                                        if (ultimaConsultaReceta != null) {
                                            val doctor = listaDoctores.find { it.id == paciente.idDoctorAsignado }
                                            if (doctor != null && doctor.activo) {
                                                val recetaFile = RecetaGenerator.generarRecetaPDF(
                                                    context = context,
                                                    consulta = ultimaConsultaReceta,
                                                    doctor = doctor,
                                                    paciente = paciente
                                                )
                                                
                                                // Actualizar en el hilo principal
                                                recetaGenerada = recetaFile
                                                pacienteReceta = paciente
                                                showRecetaDialog = true
                                            } else {
                                                // Mostrar error: doctor no encontrado o inactivo
                                                successMessage = if (doctor == null) "Error: Doctor no encontrado" else "Error: El doctor asignado está inactivo"
                                                showSuccessAlert = true
                                            }
                                        } else {
                                            // Mostrar error: no hay consultas
                                            successMessage = "Error: El paciente no tiene consultas registradas"
                                            showSuccessAlert = true
                                        }
                                    } catch (e: Exception) {
                                        // Mostrar error
                                        successMessage = "Error al generar receta: ${e.message}"
                                        showSuccessAlert = true
                                    } finally {
                                        isGeneratingReceta = false
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Description, contentDescription = "Descargar receta", tint = Color(0xFF183A6D))
                            }
                        } else {
                            // Para pacientes inactivos, solo mostrar botón de reactivar
                            IconButton(
                                onClick = { 
                                    pacienteAReactivar = paciente
                                    showConfirmReactivarPacienteDialog = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reactivar",
                                    tint = Color.Green
                                )
                            }
                            
                            // Botón de eliminar completamente (sigue disponible para pacientes inactivos)
                            IconButton({ 
                                pacienteAEliminar = paciente
                                showConfirmDeleteDialog = true
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
        if (listaDoctores.none { it.especialidad.isNotBlank() }) {
            Spacer(Modifier.height(16.dp))
            Text("Cargando especialidades...", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
    
    // Formularios usando los componentes extraídos
    if (showAddDialog) {
        AddPacienteForm(
            onDismiss = { showAddDialog = false },
            onPacienteAgregado = {
                viewModel.agregarPaciente(it)
                successMessage = "Paciente agregado exitosamente"
                showSuccessAlert = true
                showAddDialog = false
            },
            listaDoctores = listaDoctores
        )
    }
    
    if (pacienteEditando != null) {
        EditPacienteForm(
            paciente = pacienteEditando!!,
            onDismiss = { pacienteEditando = null },
            onPacienteEditado = { pacienteActualizado ->
                viewModel.actualizarPaciente(pacienteActualizado)
                successMessage = "Información del paciente actualizada exitosamente"
                showSuccessAlert = true
                pacienteEditando = null
            }
        )
    }
    
    if (pacienteParaRadiografia != null) {
        AddRadiografiaForm(
            paciente = pacienteParaRadiografia!!,
            doctor = listaDoctores.find { it.id == pacienteParaRadiografia!!.idDoctorAsignado && it.activo },
            onDismiss = { pacienteParaRadiografia = null },
            onRadiografiaSubida = { radiografia ->
                CoroutineScope(Dispatchers.IO).launch {
                    RadiografiaRepository().agregarRadiografia(radiografia)
                }
                successMessage = "Radiografía subida exitosamente"
                showSuccessAlert = true
                pacienteParaRadiografia = null
            }
        )
    }
    
    if (pacienteParaCirugia != null) {
        AddCirugiaForm(
            paciente = pacienteParaCirugia!!,
            doctor = listaDoctores.find { it.id == pacienteParaCirugia!!.idDoctorAsignado && it.activo },
            onDismiss = { 
                pacienteParaCirugia = null 
                ultimaConsultaCirugia = null // Limpiar también la última consulta
            },
            onCirugiaAgregada = { cirugia ->
                CoroutineScope(Dispatchers.IO).launch {
                    CirugiaRepository().agregarCirugia(cirugia)
                }
                successMessage = "Cirugía registrada exitosamente"
                showSuccessAlert = true
                pacienteParaCirugia = null
                ultimaConsultaCirugia = null // Limpiar también la última consulta
            },
            ultimaConsulta = ultimaConsultaCirugia,
            onActualizarUltimaConsulta = { consultaActualizada ->
                // Actualizar la última consulta en la base de datos
                CoroutineScope(Dispatchers.IO).launch {
                    ConsultaRepository().actualizarConsulta(consultaActualizada)
                }
            }
        )
    }
    
    // Alerta de éxito
    if (showSuccessAlert) {
        AlertDialog(
            onDismissRequest = { showSuccessAlert = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("¡Éxito!", fontWeight = FontWeight.Bold, color = Color.Green)
                }
            },
            text = { Text(successMessage) },
            confirmButton = {
                Button(
                    onClick = { showSuccessAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
    
    // Diálogo de confirmación de eliminación
    if (showConfirmDeleteDialog && pacienteAEliminar != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(8.dp))
                    Text("¿Eliminar paciente?", fontWeight = FontWeight.Bold)
                }
            },
            text = { 
                Text(
                    "¿Estás seguro de que deseas eliminar completamente a ${pacienteAEliminar?.nombre} ${pacienteAEliminar?.apellido}?\n\n" +
                    "⚠️ Esta acción eliminará permanentemente:\n" +
                    "• Todos los datos del paciente\n" +
                    "• Todas las consultas médicas\n" +
                    "• Todas las radiografías\n" +
                    "• Todas las cirugías\n\n" +
                    "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarPaciente(pacienteAEliminar!!.id)
                        successMessage = "Paciente y todos sus datos relacionados eliminados exitosamente"
                        showSuccessAlert = true
                        showConfirmDeleteDialog = false
                        pacienteAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmDeleteDialog = false
                    pacienteAEliminar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de receta generada
    if (showRecetaDialog && recetaGenerada != null) {
        RecetaDialog(
            onDismiss = { 
                showRecetaDialog = false 
                recetaGenerada = null
                pacienteReceta = null
            },
            recetaFile = recetaGenerada!!,
            numeroPaciente = pacienteReceta?.telefono
        )
    }
    
    // Indicador de carga para generación de receta
    if (isGeneratingReceta) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Generando receta...") },
            text = { 
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF183A6D)
                    )
                    Text("Por favor espera mientras se genera la receta médica")
                }
            },
            confirmButton = { }
        )
    }

    // Diálogo de confirmación para dar de baja paciente
    if (showConfirmBajaPacienteDialog && pacienteABajar != null) {
        AlertDialog(
            onDismissRequest = { showConfirmBajaPacienteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DoNotDisturb, contentDescription = null, tint = Color.Blue)
                    Spacer(Modifier.width(8.dp))
                    Text("¿Dar de baja paciente?", fontWeight = FontWeight.Bold)
                }
            },
            text = { 
                Text("¿Estás seguro de que deseas dar de baja a ${pacienteABajar?.nombre} ${pacienteABajar?.apellido}? Podrás reactivarlo más tarde.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.darBajaPaciente(pacienteABajar!!.id)
                        successMessage = "Paciente dado de baja exitosamente"
                        showSuccessAlert = true
                        showConfirmBajaPacienteDialog = false
                        pacienteABajar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Dar de baja", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmBajaPacienteDialog = false
                    pacienteABajar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para reactivar paciente
    if (showConfirmReactivarPacienteDialog && pacienteAReactivar != null) {
        AlertDialog(
            onDismissRequest = { showConfirmReactivarPacienteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Green)
                    Spacer(Modifier.width(8.dp))
                    Text("¿Reactivar paciente?", fontWeight = FontWeight.Bold)
                }
            },
            text = { 
                Text("¿Estás seguro de que deseas reactivar a ${pacienteAReactivar?.nombre} ${pacienteAReactivar?.apellido}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reactivarPaciente(pacienteAReactivar!!.id)
                        successMessage = "Paciente reactivado exitosamente"
                        showSuccessAlert = true
                        showConfirmReactivarPacienteDialog = false
                        pacienteAReactivar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Reactivar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmReactivarPacienteDialog = false
                    pacienteAReactivar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DoctoresSecretariaContent(
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showConfirmBajaDialog by remember { mutableStateOf(false) }
    var showConfirmReactivarDialog by remember { mutableStateOf(false) }
    var doctorAEliminar by remember { mutableStateOf<com.tuempresa.medicalapp.data.models.Doctor?>(null) }
    var doctorABajar by remember { mutableStateOf<com.tuempresa.medicalapp.data.models.Doctor?>(null) }
    var doctorAReactivar by remember { mutableStateOf<com.tuempresa.medicalapp.data.models.Doctor?>(null) }
    
    // Estados para búsqueda
    var searchText by remember { mutableStateOf("") }
    var searchByEspecialidad by remember { mutableStateOf(false) }

    // Cargar doctores al iniciar
    LaunchedEffect(Unit) {
        authViewModel.cargarListaDoctores()
    }

    // Filtrar doctores según búsqueda
    val doctoresFiltrados = uiState.listaDoctores.filter { doctor ->
        if (searchText.isBlank()) {
            true
        } else {
            if (searchByEspecialidad) {
                doctor.especialidad.contains(searchText, ignoreCase = true)
            } else {
                (doctor.nombre + " " + doctor.apellido).contains(searchText, ignoreCase = true)
            }
        }
    }.filter { it.rol == "doctor" } // Solo doctores, no secretarias

    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Doctores", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Button(onClick = { showAddUserDialog = true }) {
                Icon(Icons.Default.Person, contentDescription = "Agregar")
                Spacer(Modifier.width(8.dp))
                Text("Agregar")
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        // Buscador
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { 
                    Text(if (searchByEspecialidad) "Buscar por especialidad" else "Buscar por nombre")
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { searchByEspecialidad = !searchByEspecialidad },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (searchByEspecialidad) Color(0xFF183A6D) else Color.Gray
                )
            ) {
                Text(if (searchByEspecialidad) "Especialidad" else "Nombre", fontSize = 12.sp)
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        LazyColumn {
            items(doctoresFiltrados) { doctor ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (doctor.activo) Color.White else Color(0xFFF5F5F5)
                    )
                ) {
                    Row(
                        Modifier.padding(12.dp), 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Foto del doctor
                        if (doctor.fotoBase64.isNotBlank()) {
                            val bitmap = ImageUtils.decodeBase64ToBitmap(doctor.fotoBase64)
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Foto doctor",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color(0xFF183A6D), CircleShape)
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color(0xFF183A6D), CircleShape),
                                    tint = Color(0xFF183A6D)
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFF183A6D), CircleShape),
                                tint = Color(0xFF183A6D)
                            )
                        }
                        
                        Spacer(Modifier.width(12.dp))
                        
                        // Información del doctor
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${doctor.nombre} ${doctor.apellido}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                doctor.especialidad,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                doctor.correo,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                            if (!doctor.activo) {
                                Text(
                                    "INACTIVO",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        // Botones de acción
                        Column {
                            if (doctor.activo) {
                                // Botón de baja lógica
                                IconButton(
                                    onClick = { 
                                        doctorABajar = doctor
                                        showConfirmBajaDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.DoNotDisturb,
                                        contentDescription = "Dar de baja",
                                        tint = Color.Green
                                    )
                                }
                            } else {
                                // Botón de reactivar
                                IconButton(
                                    onClick = { 
                                        doctorAReactivar = doctor
                                        showConfirmReactivarDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Reactivar",
                                        tint = Color.Green
                                    )
                                }
                            }
                            
                            // Botón de eliminar
                            IconButton(
                                onClick = { 
                                    doctorAEliminar = doctor
                                    showConfirmDeleteDialog = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para agregar doctor
    if (showAddUserDialog) {
        AddDoctorForm(
            onDismiss = { showAddUserDialog = false },
            onDoctorAgregado = { nuevoDoctor ->
                authViewModel.crearNuevoDoctor(nuevoDoctor, nuevoDoctor.contrasena)
                successMessage = "Doctor agregado exitosamente"
                showSuccessAlert = true
                showAddUserDialog = false
            }
        )
    }
    
    // Diálogo de confirmación para eliminar
    if (showConfirmDeleteDialog && doctorAEliminar != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(8.dp))
                    Text("¿Eliminar doctor?", fontWeight = FontWeight.Bold)
                }
            },
            text = { 
                Text("¿Estás seguro de que deseas eliminar completamente a ${doctorAEliminar?.nombre} ${doctorAEliminar?.apellido}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.eliminarDoctor(doctorAEliminar!!.id)
                        successMessage = "Doctor eliminado exitosamente"
                        showSuccessAlert = true
                        showConfirmDeleteDialog = false
                        doctorAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmDeleteDialog = false
                    doctorAEliminar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para dar de baja
    if (showConfirmBajaDialog && doctorABajar != null) {
        AlertDialog(
            onDismissRequest = { showConfirmBajaDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DoNotDisturb, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(8.dp))
                    Text("¿Dar de baja?", fontWeight = FontWeight.Bold)
                }
            },
            text = { 
                Text("¿Estás seguro de que deseas dar de baja a ${doctorABajar?.nombre} ${doctorABajar?.apellido}? Podrás reactivarlo más tarde.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.darBajaDoctor(doctorABajar!!.id)
                        successMessage = "Doctor dado de baja exitosamente"
                        showSuccessAlert = true
                        showConfirmBajaDialog = false
                        doctorABajar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Dar de baja", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmBajaDialog = false
                    doctorABajar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para reactivar
    if (showConfirmReactivarDialog && doctorAReactivar != null) {
        AlertDialog(
            onDismissRequest = { showConfirmReactivarDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Green)
                    Spacer(Modifier.width(8.dp))
                    Text("¿Reactivar doctor?", fontWeight = FontWeight.Bold)
                }
            },
            text = { 
                Text("¿Estás seguro de que deseas reactivar a ${doctorAReactivar?.nombre} ${doctorAReactivar?.apellido}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.reactivarDoctor(doctorAReactivar!!.id)
                        successMessage = "Doctor reactivado exitosamente"
                        showSuccessAlert = true
                        showConfirmReactivarDialog = false
                        doctorAReactivar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Reactivar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmReactivarDialog = false
                    doctorAReactivar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Alerta de éxito
    if (showSuccessAlert) {
        AlertDialog(
            onDismissRequest = { showSuccessAlert = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("¡Éxito!", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            text = { Text(successMessage) },
            confirmButton = {
                Button(
                    onClick = { showSuccessAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
} 