package com.tuempresa.medicalapp.presentation.screens.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import com.tuempresa.medicalapp.R
import com.tuempresa.medicalapp.data.models.Consulta
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.repositories.ConsultaRepository
import com.tuempresa.medicalapp.core.utils.ProyeccionUtils
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.tuempresa.medicalapp.data.models.Doctor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialConsultasScreen(
    pacienteId: String,
    paciente: Paciente? = null,
    doctor: Doctor? = null,
    onBack: () -> Unit = {},
    onConsultaClick: (Consulta) -> Unit = {}
) {
    val blue = Color(0xFF183A6D)
    val consultaRepository = remember { ConsultaRepository() }
    var consultas by remember { mutableStateOf<List<Consulta>>(emptyList()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val datePickerState = rememberDatePickerState()

    // Cargar consultas del paciente
    LaunchedEffect(pacienteId) {
        println("DEBUG: HistorialConsultasScreen - pacienteId recibido: $pacienteId")
        scope.launch {
            try {
                val consultasPaciente = consultaRepository.obtenerConsultasPorPaciente(pacienteId)
                println("DEBUG: HistorialConsultasScreen - consultas obtenidas: ${consultasPaciente.size}")
                consultas = consultasPaciente
            } catch (e: Exception) {
                println("DEBUG: HistorialConsultasScreen - Error: ${e.message}")
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Filtrar consultas por fecha seleccionada
    val filteredConsultas = if (selectedDate.isNotEmpty()) {
        consultas.filter { it.fecha == selectedDate }
    } else {
        consultas
    }

    // Función para manejar el clic en una consulta
    val handleConsultaClick = { consulta: Consulta ->
        // Enviar consulta específica a la Smart TV
        if (paciente != null) {
            ProyeccionUtils.enviarConsultaEspecifica(
                context = context,
                paciente = paciente,
                consulta = consulta,
                doctor = doctor
            )
        }
        // Llamar al callback original si existe
        onConsultaClick(consulta)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Botón de regreso
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = blue,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Título con ícono
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_consultas),
                    contentDescription = "Icono Consultas",
                    modifier = Modifier.size(32.dp),
                    tint = blue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Historial de Consultas",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    color = blue
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Filtro de fecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFDDEBFA)),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .height(48.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar_1),
                    contentDescription = "Filtrar por fecha",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedDate.isNotEmpty()) selectedDate else "Filtrar por fecha",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = blue
                )
            }
        }
        
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val safeMillis = millis + 12 * 60 * 60 * 1000
                            val localDate = Instant.ofEpochMilli(safeMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            selectedDate = formattedDate
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        // Recuadro para las cards
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE6F0FA))
                .padding(12.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = blue)
                }
            } else if (filteredConsultas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay consultas registradas",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredConsultas) { consulta ->
                        ConsultaCard(
                            consulta = consulta,
                            onClick = { handleConsultaClick(consulta) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultaCard(consulta: Consulta, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_diagnostico),
                        contentDescription = "Motivo de consulta",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Motivo: ${consulta.motivo}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_1),
                        contentDescription = "Fecha de consulta",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fecha: ${consulta.fecha}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Hora de consulta",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hora: ${consulta.hora}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver más",
                tint = Color(0xFF183A6D),
                modifier = Modifier.size(32.dp)
            )
        }
    }
} 