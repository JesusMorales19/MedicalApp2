package com.tuempresa.medicalapp.presentation.components.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.medicalapp.data.models.Cirugia
import com.tuempresa.medicalapp.data.models.Doctor
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.models.Consulta
import com.tuempresa.medicalapp.data.models.Medicamento
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCirugiaForm(
    paciente: Paciente,
    doctor: Doctor?,
    onDismiss: () -> Unit,
    onCirugiaAgregada: (Cirugia) -> Unit,
    ultimaConsulta: Consulta? = null,
    onActualizarUltimaConsulta: ((Consulta) -> Unit)? = null
) {
    // Estados para los campos del formulario
    var nombreCirugia by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var tipoProcesamiento by remember { mutableStateOf("") }
    var tiempoRecuperacion by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var procedimiento by remember { mutableStateOf("") }
    var pronostico by remember { mutableStateOf("") }
    var complicaciones by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf("") }
    var quirofano by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Estados para medicamentos
    var medicamentos by remember { mutableStateOf(listOf<Medicamento>()) }
    var nombreMedicamento by remember { mutableStateOf("") }
    var dosisMedicamento by remember { mutableStateOf("") }
    var frecuenciaMedicamento by remember { mutableStateOf("") }
    var viaAdministracionMedicamento by remember { mutableStateOf("") }
    var diasPrescripcionMedicamento by remember { mutableStateOf("") }
    
    // Estados para medicamentos de la última consulta
    var medicamentosUltimaConsulta by remember { mutableStateOf(listOf<Medicamento>()) }
    var datosCargados by remember { mutableStateOf(false) }
    var editandoMedicamentoSeleccionado by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Cirugía", fontWeight = FontWeight.Bold, color = Color(0xFF183A6D)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // Datos auto-llenados
                Text("Paciente: ${paciente.nombre} ${paciente.apellido}")
                Text("ID: ${paciente.id}")
                Text("Doctor: ${doctor?.nombre ?: ""} ${doctor?.apellido ?: ""}")
                Spacer(Modifier.height(8.dp))
                
                // Campos del formulario
                OutlinedTextField(
                    value = nombreCirugia,
                    onValueChange = { nombreCirugia = it },
                    label = { Text("Nombre de la cirugía") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = fecha,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de la cirugía (dd/MM/yyyy)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showDatePicker = true }
                )
                
                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
                    label = { Text("Hospital") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = quirofano,
                    onValueChange = { quirofano = it },
                    label = { Text("Quirófano") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = tipoProcesamiento,
                    onValueChange = { tipoProcesamiento = it },
                    label = { Text("Tipo de procesamiento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = tiempoRecuperacion,
                    onValueChange = { tiempoRecuperacion = it },
                    label = { Text("Tiempo de recuperación") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo de la cirugía") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = procedimiento,
                    onValueChange = { procedimiento = it },
                    label = { Text("Procedimiento") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = pronostico,
                    onValueChange = { pronostico = it },
                    label = { Text("Pronóstico") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = complicaciones,
                    onValueChange = { complicaciones = it },
                    label = { Text("Complicaciones (si las hay)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sección de Medicamentos
                Text("Medicamentos:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                
                // Botón para cargar medicamentos de la última consulta
                if (!datosCargados && ultimaConsulta != null) {
                    Button(
                        onClick = {
                            // Cargar medicamentos de la última consulta
                            medicamentosUltimaConsulta = ultimaConsulta.medicamentos ?: emptyList()
                            medicamentos = medicamentosUltimaConsulta.toMutableList()
                            datosCargados = true
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("Cargar medicamentos de la última consulta")
                    }
                } else if (ultimaConsulta == null) {
                    Text(
                        "No hay consultas previas para este paciente",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else if (datosCargados) {
                    Text(
                        "Medicamentos de la última consulta cargados",
                        color = Color(0xFF183A6D),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Mostrar medicamentos de la última consulta si están cargados
                if (datosCargados && medicamentosUltimaConsulta.isNotEmpty()) {
                    Text(
                        "Medicamentos de la última consulta:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF183A6D),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Column(
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        medicamentosUltimaConsulta.forEach { medicamento ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            medicamento.nombre,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row {
                                            IconButton(
                                                onClick = {
                                                    // Cargar datos del medicamento en el formulario de edición
                                                    nombreMedicamento = medicamento.nombre
                                                    dosisMedicamento = medicamento.dosis
                                                    frecuenciaMedicamento = medicamento.frecuencia
                                                    viaAdministracionMedicamento = medicamento.viaAdministracion
                                                    diasPrescripcionMedicamento = medicamento.diasPrescripcion
                                                    editandoMedicamentoSeleccionado = medicamento.nombre
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Editar",
                                                    tint = Color.Gray
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    medicamentos = medicamentos.filter { it.nombre != medicamento.nombre }
                                                    medicamentosUltimaConsulta = medicamentosUltimaConsulta.filter { it.nombre != medicamento.nombre }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                    
                                    Text("Dosis: ${medicamento.dosis}")
                                    Text("Frecuencia: ${medicamento.frecuencia}")
                                    Text("Vía: ${medicamento.viaAdministracion}")
                                    Text("Días: ${medicamento.diasPrescripcion}")
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Text(
                    if (editandoMedicamentoSeleccionado != null) "Editar medicamento:" else "Agregar nuevos medicamentos:",
                    fontWeight = FontWeight.Bold,
                    color = if (editandoMedicamentoSeleccionado != null) Color(0xFF183A6D) else Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                OutlinedTextField(
                    value = nombreMedicamento,
                    onValueChange = { if (editandoMedicamentoSeleccionado == null) nombreMedicamento = it },
                    label = { Text("Nombre del medicamento") },
                    readOnly = editandoMedicamentoSeleccionado != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = dosisMedicamento,
                    onValueChange = { dosisMedicamento = it },
                    label = { Text("Dosis") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = frecuenciaMedicamento,
                    onValueChange = { frecuenciaMedicamento = it },
                    label = { Text("Frecuencia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                // Vía de administración (dropdown)
                val viasAdministracion = listOf("Oral", "Intravenosa", "Intramuscular", "Subcutánea", "Tópica", "Inhalada", "Rectal", "Ocular", "Nasal")
                var expandedViaAdministracion by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    OutlinedTextField(
                        value = viaAdministracionMedicamento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vía de administración") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedViaAdministracion) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir",
                                modifier = Modifier.clickable { expandedViaAdministracion = !expandedViaAdministracion }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedViaAdministracion = !expandedViaAdministracion }
                    )
                    DropdownMenu(
                        expanded = expandedViaAdministracion,
                        onDismissRequest = { expandedViaAdministracion = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        viasAdministracion.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viaAdministracionMedicamento = option
                                    expandedViaAdministracion = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = diasPrescripcionMedicamento,
                    onValueChange = { diasPrescripcionMedicamento = it },
                    label = { Text("Días de prescripción") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (nombreMedicamento.isNotBlank() && dosisMedicamento.isNotBlank() && frecuenciaMedicamento.isNotBlank()) {
                                if (editandoMedicamentoSeleccionado != null) {
                                    // Modo edición - actualizar medicamento existente
                                    val medicamentoActualizado = Medicamento(
                                        nombre = nombreMedicamento,
                                        dosis = dosisMedicamento,
                                        frecuencia = frecuenciaMedicamento,
                                        viaAdministracion = viaAdministracionMedicamento,
                                        diasPrescripcion = diasPrescripcionMedicamento
                                    )
                                    medicamentos = medicamentos.map { 
                                        if (it.nombre == editandoMedicamentoSeleccionado) medicamentoActualizado else it 
                                    }
                                    medicamentosUltimaConsulta = medicamentosUltimaConsulta.map { 
                                        if (it.nombre == editandoMedicamentoSeleccionado) medicamentoActualizado else it 
                                    }
                                    editandoMedicamentoSeleccionado = null
                                } else {
                                    // Modo agregar - agregar nuevo medicamento
                                    val medicamento = Medicamento(
                                        nombre = nombreMedicamento,
                                        dosis = dosisMedicamento,
                                        frecuencia = frecuenciaMedicamento,
                                        viaAdministracion = viaAdministracionMedicamento,
                                        diasPrescripcion = diasPrescripcionMedicamento
                                    )
                                    medicamentos = medicamentos + medicamento
                                }
                                nombreMedicamento = ""
                                dosisMedicamento = ""
                                frecuenciaMedicamento = ""
                                viaAdministracionMedicamento = ""
                                diasPrescripcionMedicamento = ""
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (editandoMedicamentoSeleccionado != null) "Actualizar medicamento" else "Agregar medicamento")
                    }
                    
                    if (editandoMedicamentoSeleccionado != null) {
                        Button(
                            onClick = {
                                editandoMedicamentoSeleccionado = null
                                nombreMedicamento = ""
                                dosisMedicamento = ""
                                frecuenciaMedicamento = ""
                                viaAdministracionMedicamento = ""
                                diasPrescripcionMedicamento = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
                
                // Lista de todos los medicamentos (última consulta + nuevos)
                if (medicamentos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Todos los medicamentos:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    
                    Column(
                        modifier = Modifier
                            .heightIn(max = 150.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        medicamentos.forEach { medicamento ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4FD))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(medicamento.nombre, fontWeight = FontWeight.Bold)
                                        Text("Dosis: ${medicamento.dosis}")
                                        Text("Frecuencia: ${medicamento.frecuencia}")
                                        Text("Vía: ${medicamento.viaAdministracion}")
                                        Text("Días: ${medicamento.diasPrescripcion}")
                                    }
                                    if (!medicamentosUltimaConsulta.any { it.nombre == medicamento.nombre }) {
                                        IconButton(
                                            onClick = {
                                                medicamentos = medicamentos.filter { it.nombre != medicamento.nombre }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
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
                
                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red, fontSize = 12.sp)
                }
                
                // DatePicker para fecha de cirugía
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val safeMillis = millis + 12 * 60 * 60 * 1000 // Suma 12 horas para evitar desfase
                                    val localDate = Instant.ofEpochMilli(safeMillis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    fecha = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombreCirugia.isBlank() || fecha.isBlank() || hospital.isBlank() || motivo.isBlank() || procedimiento.isBlank()) {
                        error = "Los campos nombre, fecha, hospital, motivo y procedimiento son obligatorios"
                        return@Button
                    }
                                         // Crear la cirugía
                     val cirugia = Cirugia(
                         id = "",
                         pacienteId = paciente.id,
                         doctorId = doctor?.id ?: "",
                         nombreDoctor = "${doctor?.nombre ?: ""} ${doctor?.apellido ?: ""}".trim(),
                         nombreCirugia = nombreCirugia,
                         fecha = fecha,
                         hospital = hospital,
                         quirofano = quirofano,
                         tipoProcesamiento = tipoProcesamiento,
                         tiempoRecuperacion = tiempoRecuperacion,
                         motivo = motivo,
                         procedimiento = procedimiento,
                         pronostico = pronostico,
                         complicaciones = complicaciones,
                         medicamentos = medicamentos,
                         fechaCreacion = System.currentTimeMillis()
                     )
                     
                     // Actualizar la última consulta con los nuevos medicamentos si existe
                     if (ultimaConsulta != null && medicamentos.isNotEmpty()) {
                         val consultaActualizada = ultimaConsulta.copy(
                             medicamentos = medicamentos
                         )
                         onActualizarUltimaConsulta?.invoke(consultaActualizada)
                     }
                     
                     onCirugiaAgregada(cirugia)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183A6D))
            ) {
                Text("Registrar Cirugía", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF183A6D))
            }
        }
    )
} 