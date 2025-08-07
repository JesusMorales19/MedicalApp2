package com.tuempresa.medicalapp.presentation.screens.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.medicalapp.R
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuempresa.medicalapp.presentation.viewmodels.PacientesViewModel
import com.tuempresa.medicalapp.data.models.Paciente
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import com.tuempresa.medicalapp.core.network.ProyeccionService
import android.util.Log
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.delay
import com.tuempresa.medicalapp.presentation.components.forms.AddConsultaForm
import com.tuempresa.medicalapp.data.repositories.ConsultaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle

@Composable
fun base64ToImageBitmap(base64Str: String): ImageBitmap? {
    return try {
        if (base64Str.isBlank()) return null
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

// Modifica PacientesScreen para aceptar un Modifier
@Composable
fun PacientesScreen(
    modifier: Modifier = Modifier,
    onPacienteClick: (Paciente) -> Unit = {},
    authViewModel: com.tuempresa.medicalapp.presentation.viewmodels.AuthViewModel? = null
) {
    val pacientesViewModel: PacientesViewModel = viewModel()
    val pacientes by pacientesViewModel.pacientes.collectAsState()
    val consultas by pacientesViewModel.consultas.collectAsState()
    val authUiState by authViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(null) }
    val context = LocalContext.current
    var showProyeccion by remember { mutableStateOf(false) }
    var serverStatus by remember { mutableStateOf("Desconectado") }
    
    // Estados para agregar consulta
    var pacienteParaConsulta by remember { mutableStateOf<Paciente?>(null) }
    var ultimaConsulta by remember { mutableStateOf<com.tuempresa.medicalapp.data.models.Consulta?>(null) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    
    // Broadcast receiver para recibir actualizaciones del estado del servidor
    val serverStatusReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ProyeccionService.ACTION_PORT_UPDATE) {
                    val port = intent.getIntExtra(ProyeccionService.EXTRA_PORT, -1)
                    val status = intent.getStringExtra(ProyeccionService.EXTRA_STATUS) ?: "Desconectado"
                    
                    Log.i("PacientsScreen", "📡 Broadcast recibido - Puerto: $port, Estado: $status")
                    serverStatus = status
                    
                    // Si el puerto es -1 o el estado es "Desconectado", verificar si el servicio realmente se detuvo
                    if (port == -1 || status == "Desconectado") {
                        val isRunning = ProyeccionService.isServiceRunning(context)
                        if (!isRunning) {
                            serverStatus = "Desconectado"
                            Log.i("PacientsScreen", "✅ Servicio confirmado como detenido")
                        }
                    }
                }
            }
        }
    }

    // Verificar el estado inicial del servicio cuando se carga la pantalla
    LaunchedEffect(Unit) {
        val isRunning = ProyeccionService.isServiceRunning(context)
        if (isRunning) {
            serverStatus = "Conectado"
        }
        Log.i("PacientsScreen", "Estado inicial del servicio: $isRunning")
    }

    // Verificar periódicamente el estado del servicio para mantener el icono actualizado
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // Verificar cada 2 segundos
            val isRunning = ProyeccionService.isServiceRunning(context)
            val shouldBeConnected = isRunning
            val currentStatus = serverStatus == "Conectado"
            
            if (shouldBeConnected != currentStatus) {
                Log.i("PacientsScreen", "🔄 Estado del servicio cambiado - Ejecutándose: $isRunning")
                serverStatus = if (isRunning) "Conectado" else "Desconectado"
            }
        }
    }

    // Registrar el broadcast receiver
    DisposableEffect(context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            serverStatusReceiver,
            IntentFilter(ProyeccionService.ACTION_PORT_UPDATE)
        )
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(serverStatusReceiver)
        }
    }
    
    var search by remember { mutableStateOf("") }
    var blue = Color(0xFF183A6D)
    val lightGray = Color(0xFFF5F7FA)

    // Cargar pacientes del doctor autenticado
    LaunchedEffect(authUiState?.doctor?.id) {
        val doctorId = authUiState?.doctor?.id
        if (doctorId != null && doctorId.isNotEmpty()) {
            pacientesViewModel.cargarPacientesPorDoctor(doctorId)
        } else {
            // Si no hay doctor autenticado, cargar todos los pacientes (para secretaria)
            pacientesViewModel.cargarPacientes()
        }
    }

    // Filtrar pacientes por nombre o fecha de registro
    val pacientesFiltrados = pacientes.filter {
        it.nombre.contains(search, ignoreCase = true) ||
        it.apellido.contains(search, ignoreCase = true) ||
        it.fechaCreacionStr.contains(search, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .background(lightGray)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        // Barra de búsqueda
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Buscar...", color = Color(0xFFA4C8DF), fontSize = 18.sp) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Buscar",
                    tint = Color(0xFFA4C8DF)
                )
            },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = blue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )
        Spacer(modifier = Modifier.height(26.dp))
        // Título con icono de proyección solo para doctor
        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (authUiState?.doctor?.rol == "secretaria") "Todos los Pacientes" else "Mis Pacientes",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = blue,
            )
            if (authUiState?.doctor?.rol == "doctor") {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showProyeccion = true }) {
                    Icon(
                        painter = painterResource(
                            id = if (serverStatus == "Conectado") R.drawable.server_off else R.drawable.server_on
                        ),
                        contentDescription = if (serverStatus == "Conectado") "Servidor activo" else "Iniciar servidor de proyección",
                        tint = Color.Unspecified
                    )
                }
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (pacientesFiltrados.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (authUiState?.doctor?.rol == "doctor") 
                                    "No tienes pacientes asignados" 
                                else 
                                    "No hay pacientes registrados",
                                fontSize = 18.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(pacientesFiltrados) { paciente ->
                    PacienteCard(
                        paciente = paciente,
                        onDiagnosticoClick = { onPacienteClick(paciente) },
                        onAgregarConsulta = {
                            // Obtener la última consulta del paciente antes de mostrar el formulario
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val consultas = ConsultaRepository().obtenerConsultasPorPaciente(paciente.id)
                                    // Ordenar por fecha descendente y tomar la primera (más reciente)
                                    val ultima = consultas.sortedByDescending { 
                                        try {
                                            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                            java.time.LocalDate.parse(it.fecha, formatter)
                                        } catch (e: Exception) {
                                            java.time.LocalDate.MIN // Fecha mínima si hay error
                                        }
                                    }.firstOrNull()
                                    
                                    // Actualizar el estado en el hilo principal
                                    ultimaConsulta = ultima
                                    pacienteParaConsulta = paciente
                                } catch (e: Exception) {
                                    println("Error al obtener última consulta: ${e.message}")
                                    ultimaConsulta = null
                                    pacienteParaConsulta = paciente
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    if (showProyeccion) {
        AlertDialog(
            onDismissRequest = { showProyeccion = false },
            confirmButton = {},
            dismissButton = {},
            text = {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    // Log para debuggear los datos del doctor
                    LaunchedEffect(authUiState?.doctor) {
                        Log.i("PacientsScreen", "🔍 Datos del doctor para proyección:")
                        Log.i("PacientsScreen", "   - Nombre: ${authUiState?.doctor?.nombre}")
                        Log.i("PacientsScreen", "   - Apellido: ${authUiState?.doctor?.apellido}")
                        Log.i("PacientsScreen", "   - Foto: ${authUiState?.doctor?.fotoBase64?.take(50)}...")
                        Log.i("PacientsScreen", "   - Doctor completo: ${authUiState?.doctor != null}")
                    }
                    
                    ProyeccionScreen(
                        pacientes = pacientes,
                        consultas = consultas,
                        nombreDoctor = authUiState?.doctor?.nombre ?: "",
                        apellidoDoctor = authUiState?.doctor?.apellido ?: "",
                        fotoDoctor = authUiState?.doctor?.fotoBase64 ?: ""
                    )
                }
            }
        )
    }
    
    // Formulario de consulta
    if (pacienteParaConsulta != null) {
        AddConsultaForm(
            onDismiss = { 
                pacienteParaConsulta = null 
                ultimaConsulta = null // Limpiar también la última consulta
            },
            onConsultaAgregada = { consulta ->
                CoroutineScope(Dispatchers.IO).launch {
                    ConsultaRepository().agregarConsulta(consulta)
                }
                successMessage = "Consulta registrada exitosamente"
                showSuccessAlert = true
                pacienteParaConsulta = null
                ultimaConsulta = null // Limpiar también la última consulta
            },
            pacienteId = pacienteParaConsulta!!.id,
            ultimaConsulta = ultimaConsulta,
            doctorAsignado = authUiState?.doctor
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
                    Spacer(modifier = Modifier.width(8.dp))
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

@Composable
fun PacienteCard(
    paciente: Paciente,
    onDiagnosticoClick: () -> Unit,
    onAgregarConsulta: () -> Unit = {}
) {
    val blue = Color(0xFF183A6D)
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val imageBitmap = base64ToImageBitmap(paciente.fotoBase64)
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Foto paciente",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFA4C8DF), CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_persona),
                        contentDescription = "Foto paciente",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFA4C8DF), CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${paciente.nombre} ${paciente.apellido}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = blue
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Fecha alineada a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Fecha",
                    tint = blue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Fecha de Registro: ",
                    fontSize = 17.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    paciente.fechaCreacionStr,
                    fontSize = 16.sp,
                    color = blue,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón de Ver Detalles
                Row(
                    modifier = Modifier
                        .clickable { onDiagnosticoClick() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_diagnostico),
                        contentDescription = "Diagnóstico",
                        tint = blue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Ver Detalles",
                        fontSize = 16.sp,
                        color = blue,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
                
                // Botón de Agregar Consulta
                Row(
                    modifier = Modifier
                        .clickable { onAgregarConsulta() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_icon_consultas),
                        contentDescription = "Agregar Consulta",
                        tint = blue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Agregar Consulta",
                        fontSize = 16.sp,
                        color = blue,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}
