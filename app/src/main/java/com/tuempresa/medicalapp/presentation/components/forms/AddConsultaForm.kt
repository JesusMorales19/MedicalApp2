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
import com.tuempresa.medicalapp.data.models.Consulta
import com.tuempresa.medicalapp.data.models.Medicamento
import com.tuempresa.medicalapp.data.models.Doctor
import com.tuempresa.medicalapp.core.utils.ValidationUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConsultaForm(
    onDismiss: () -> Unit,
    onConsultaAgregada: (Consulta) -> Unit,
    pacienteId: String,
    ultimaConsulta: Consulta? = null,
    doctorAsignado: Doctor? = null
) {
    var fechaConsulta by remember { mutableStateOf("") }
    var horaConsulta by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var sintomasTexto by remember { mutableStateOf("") }
    var duracionSintomas by remember { mutableStateOf("") }
    var diagnosticoPrincipal by remember { mutableStateOf("") }
    var diagnosticoSecundario by remember { mutableStateOf("") }
    var enfermedadesCronicas by remember { mutableStateOf("") }
    var alergias by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var estudios by remember { mutableStateOf("") }
    var indicaciones by remember { mutableStateOf("") }
    var requiereSeguimiento by remember { mutableStateOf(false) }
    var fechaProxima by remember { mutableStateOf("") }
    var presionArterial by remember { mutableStateOf("") }
    var temperatura by remember { mutableStateOf("") }
    var frecuenciaCardiaca by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    
    // Estados para validaciones en tiempo real
    var fechaConsultaError by remember { mutableStateOf("") }
    var horaConsultaError by remember { mutableStateOf("") }
    var motivoError by remember { mutableStateOf("") }
    var pesoError by remember { mutableStateOf("") }
    var alturaError by remember { mutableStateOf("") }
    var presionArterialError by remember { mutableStateOf("") }
    var temperaturaError by remember { mutableStateOf("") }
    var frecuenciaCardiacaError by remember { mutableStateOf("") }
    
    // Medicamentos
    var medicamentos by remember { mutableStateOf(listOf<Medicamento>()) }
    var nombreMedicamento by remember { mutableStateOf("") }
    var dosisMedicamento by remember { mutableStateOf("") }
    var frecuenciaMedicamento by remember { mutableStateOf("") }
    var viaAdministracionMedicamento by remember { mutableStateOf("") }
    var diasPrescripcionMedicamento by remember { mutableStateOf("") }
    
    // Estado para las secciones del formulario
    var currentSection by remember { mutableStateOf(0) }
    val sections = listOf("Información General", "Historial Clínico", "Signos Vitales", "Medicamentos")
    
    // Estados para DatePickers
    var showDatePickerConsulta by remember { mutableStateOf(false) }
    var showDatePickerProxima by remember { mutableStateOf(false) }
    
    // Estados para auto-llenado y edición
    var datosCargados by remember { mutableStateOf(false) }
    var editandoDiagnosticoPrincipal by remember { mutableStateOf(false) }
    var editandoDiagnosticoSecundario by remember { mutableStateOf(false) }
    var editandoEnfermedadesCronicas by remember { mutableStateOf(false) }
    var editandoAlergias by remember { mutableStateOf(false) }
    var editandoPeso by remember { mutableStateOf(false) }
    var editandoAltura by remember { mutableStateOf(false) }
    
    // Estados para medicamentos de la última consulta
    var medicamentosUltimaConsulta by remember { mutableStateOf(listOf<Medicamento>()) }
    var medicamentosEditando by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }
    var medicamentosValoresEditando by remember { mutableStateOf(mutableMapOf<String, Pair<String, String>>()) }
    var editandoMedicamentoSeleccionado by remember { mutableStateOf<String?>(null) }
    
    // Validaciones en tiempo real
    LaunchedEffect(fechaConsulta) {
        fechaConsultaError = when {
            fechaConsulta.isEmpty() -> ""
            !ValidationUtils.isValidDate(fechaConsulta) -> "Formato de fecha inválido (DD/MM/YYYY)"
            else -> ""
        }
    }
    
    LaunchedEffect(horaConsulta) {
        horaConsultaError = when {
            horaConsulta.isEmpty() -> ""
            !horaConsulta.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) -> "Formato de hora inválido (HH:MM)"
            else -> ""
        }
    }
    
    LaunchedEffect(motivo) {
        motivoError = when {
            motivo.isEmpty() -> ""
            !ValidationUtils.hasMinLength(motivo, 5) -> "El motivo debe tener al menos 5 caracteres"
            else -> ""
        }
    }
    
    LaunchedEffect(peso) {
        pesoError = when {
            peso.isEmpty() -> ""
            !ValidationUtils.isValidWeight(peso) -> "Peso debe estar entre 0.1 y 500 kg"
            else -> ""
        }
    }
    
    LaunchedEffect(altura) {
        alturaError = when {
            altura.isEmpty() -> ""
            !ValidationUtils.isValidHeight(altura) -> "Altura debe estar entre 0.1 y 3 metros"
            else -> ""
        }
    }
    
    LaunchedEffect(presionArterial) {
        presionArterialError = when {
            presionArterial.isEmpty() -> ""
            !ValidationUtils.isValidBloodPressure(presionArterial) -> "Formato de presión arterial inválido (ej: 120/80)"
            else -> ""
        }
    }
    
    LaunchedEffect(temperatura) {
        temperaturaError = when {
            temperatura.isEmpty() -> ""
            !ValidationUtils.isValidTemperature(temperatura) -> "Temperatura debe estar entre 30 y 45°C"
            else -> ""
        }
    }
    
    LaunchedEffect(frecuenciaCardiaca) {
        frecuenciaCardiacaError = when {
            frecuenciaCardiaca.isEmpty() -> ""
            !ValidationUtils.isValidHeartRate(frecuenciaCardiaca) -> "Frecuencia cardíaca debe estar entre 40 y 200 bpm"
            else -> ""
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Consulta", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // Título de la sección actual
                Text(
                    text = if (currentSection < sections.size) sections[currentSection] else "Sección",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF183A6D),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Contenido de las secciones
                when (currentSection) {
                    0 -> {
                        // Primera pestaña: Información General
                        Column {
                            // Fecha de consulta
                OutlinedTextField(
                    value = fechaConsulta,
                                onValueChange = {},
                                readOnly = true,
                    label = { Text("Fecha de consulta (dd/mm/yyyy)") },
                    singleLine = true,
                                isError = fechaConsultaError.isNotEmpty() && fechaConsulta.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showDatePickerConsulta = true }
                            )
                            
                            if (fechaConsultaError.isNotEmpty() && fechaConsulta.isNotEmpty()) {
                                Text(fechaConsultaError, color = Color.Red, fontSize = 12.sp)
                            } else if (fechaConsulta.isEmpty()) {
                                Text("Ingresa la fecha de consulta", color = Color.Gray, fontSize = 12.sp)
                            }
                            
                            // Hora de consulta
                OutlinedTextField(
                    value = horaConsulta,
                    onValueChange = { horaConsulta = it },
                    label = { Text("Hora de consulta (hh:mm)") },
                    singleLine = true,
                                isError = horaConsultaError.isNotEmpty() && horaConsulta.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                            if (horaConsultaError.isNotEmpty() && horaConsulta.isNotEmpty()) {
                                Text(horaConsultaError, color = Color.Red, fontSize = 12.sp)
                            } else if (horaConsulta.isEmpty()) {
                                Text("Ingresa la hora de consulta", color = Color.Gray, fontSize = 12.sp)
                            }
                            
                            // Motivo
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo de la consulta") },
                    singleLine = true,
                                isError = motivoError.isNotEmpty() && motivo.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                            if (motivoError.isNotEmpty() && motivo.isNotEmpty()) {
                                Text(motivoError, color = Color.Red, fontSize = 12.sp)
                            } else if (motivo.isEmpty()) {
                                Text("El motivo debe tener al menos 5 caracteres", color = Color.Gray, fontSize = 12.sp)
                            }
                            
                            // Síntomas
                OutlinedTextField(
                    value = sintomasTexto,
                    onValueChange = { sintomasTexto = it },
                    label = { Text("Síntomas (separados por comas)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                            if (sintomasTexto.isEmpty()) {
                                Text("Ingresa los síntomas del paciente", color = Color.Gray, fontSize = 12.sp)
                            }
                            
                            // Duración de síntomas
                OutlinedTextField(
                    value = duracionSintomas,
                    onValueChange = { duracionSintomas = it },
                    label = { Text("Duración de síntomas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                            // Notas
                            OutlinedTextField(
                                value = notas,
                                onValueChange = { notas = it },
                                label = { Text("Notas") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            
                            // Estudios
                            OutlinedTextField(
                                value = estudios,
                                onValueChange = { estudios = it },
                                label = { Text("Estudios solicitados") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            
                            // Indicaciones
                            OutlinedTextField(
                                value = indicaciones,
                                onValueChange = { indicaciones = it },
                                label = { Text("Indicaciones") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            
                            // Requiere seguimiento
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = requiereSeguimiento,
                                    onCheckedChange = { requiereSeguimiento = it }
                                )
                                Text("Requiere seguimiento")
                            }
                            
                            if (requiereSeguimiento) {
                                OutlinedTextField(
                                    value = fechaProxima,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Fecha próxima consulta (dd/mm/yyyy)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showDatePickerProxima = true }
                                )
                            }
                        }
                    }
                    1 -> {
                        // Segunda pestaña: Historial Clínico
                        Column {
                            Text("Historial Clínico del Paciente:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                            
                            // Botón para cargar datos de la última consulta
                            if (!datosCargados && ultimaConsulta != null) {
                                Button(
                                    onClick = {
                                        // Cargar datos reales de la última consulta
                                        diagnosticoPrincipal = ultimaConsulta.diagnosticoPrincipal ?: ""
                                        diagnosticoSecundario = ultimaConsulta.diagnosticoSecundario ?: ""
                                        enfermedadesCronicas = ultimaConsulta.enfermedadesCronicas ?: ""
                                        alergias = ultimaConsulta.alergias ?: ""
                                        peso = ultimaConsulta.peso.toString()
                                        altura = ultimaConsulta.altura.toString()
                                        
                                        // Cargar medicamentos de la última consulta
                                        medicamentosUltimaConsulta = ultimaConsulta.medicamentos ?: emptyList()
                                        medicamentos = medicamentosUltimaConsulta.toMutableList()
                                        
                                        // Inicializar estados de edición para medicamentos
                                        medicamentosUltimaConsulta.forEach { medicamento ->
                                            medicamentosEditando[medicamento.nombre] = false
                                            medicamentosValoresEditando[medicamento.nombre] = Pair(medicamento.dosis, medicamento.frecuencia)
                                        }
                                        
                                        datosCargados = true
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Text("Cargar datos de la última consulta")
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
                                    "Datos de la última consulta cargados",
                                    color = Color(0xFF183A6D),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                                                         // Diagnóstico principal - editable directamente si no hay consultas previas
                             if (ultimaConsulta == null) {
                OutlinedTextField(
                    value = diagnosticoPrincipal,
                    onValueChange = { diagnosticoPrincipal = it },
                    label = { Text("Diagnóstico principal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                             } else {
                                 Row(
                                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                     verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                 ) {
                                     OutlinedTextField(
                                         value = diagnosticoPrincipal,
                                         onValueChange = { if (editandoDiagnosticoPrincipal) diagnosticoPrincipal = it },
                                         readOnly = !editandoDiagnosticoPrincipal,
                                         label = { Text("Diagnóstico principal") },
                                         singleLine = true,
                                         modifier = Modifier.weight(1f)
                                     )
                                     IconButton(onClick = { editandoDiagnosticoPrincipal = !editandoDiagnosticoPrincipal }) {
                                         Icon(
                                             imageVector = Icons.Default.Edit,
                                             contentDescription = "Editar",
                                             tint = if (editandoDiagnosticoPrincipal) Color(0xFF183A6D) else Color.Gray
                                         )
                                     }
                                 }
                             }
                            
                                                         // Diagnóstico secundario - editable directamente si no hay consultas previas
                             if (ultimaConsulta == null) {
                OutlinedTextField(
                    value = diagnosticoSecundario,
                    onValueChange = { diagnosticoSecundario = it },
                    label = { Text("Diagnóstico secundario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                             } else {
                                 Row(
                                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                     verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                 ) {
                                     OutlinedTextField(
                                         value = diagnosticoSecundario,
                                         onValueChange = { if (editandoDiagnosticoSecundario) diagnosticoSecundario = it },
                                         readOnly = !editandoDiagnosticoSecundario,
                                         label = { Text("Diagnóstico secundario") },
                                         singleLine = true,
                                         modifier = Modifier.weight(1f)
                                     )
                                     IconButton(onClick = { editandoDiagnosticoSecundario = !editandoDiagnosticoSecundario }) {
                                         Icon(
                                             imageVector = Icons.Default.Edit,
                                             contentDescription = "Editar",
                                             tint = if (editandoDiagnosticoSecundario) Color(0xFF183A6D) else Color.Gray
                                         )
                                     }
                                 }
                             }
                            
                                                         // Enfermedades crónicas - editable directamente si no hay consultas previas
                             if (ultimaConsulta == null) {
                OutlinedTextField(
                    value = enfermedadesCronicas,
                    onValueChange = { enfermedadesCronicas = it },
                    label = { Text("Enfermedades crónicas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                             } else {
                                 Row(
                                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                     verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                 ) {
                                     OutlinedTextField(
                                         value = enfermedadesCronicas,
                                         onValueChange = { if (editandoEnfermedadesCronicas) enfermedadesCronicas = it },
                                         readOnly = !editandoEnfermedadesCronicas,
                                         label = { Text("Enfermedades crónicas") },
                                         singleLine = true,
                                         modifier = Modifier.weight(1f)
                                     )
                                     IconButton(onClick = { editandoEnfermedadesCronicas = !editandoEnfermedadesCronicas }) {
                                         Icon(
                                             imageVector = Icons.Default.Edit,
                                             contentDescription = "Editar",
                                             tint = if (editandoEnfermedadesCronicas) Color(0xFF183A6D) else Color.Gray
                                         )
                                     }
                                 }
                             }
                            
                                                         // Alergias - editable directamente si no hay consultas previas
                             if (ultimaConsulta == null) {
                OutlinedTextField(
                    value = alergias,
                    onValueChange = { alergias = it },
                    label = { Text("Alergias") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                             } else {
                                 Row(
                                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                     verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                 ) {
                                     OutlinedTextField(
                                         value = alergias,
                                         onValueChange = { if (editandoAlergias) alergias = it },
                                         readOnly = !editandoAlergias,
                                         label = { Text("Alergias") },
                                         singleLine = true,
                                         modifier = Modifier.weight(1f)
                                     )
                                     IconButton(onClick = { editandoAlergias = !editandoAlergias }) {
                                         Icon(
                                             imageVector = Icons.Default.Edit,
                                             contentDescription = "Editar",
                                             tint = if (editandoAlergias) Color(0xFF183A6D) else Color.Gray
                                         )
                                     }
                                 }
                             }
                            
                                                         // Peso - editable directamente si no hay consultas previas
                             if (ultimaConsulta == null) {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso (kg)") },
                    singleLine = true,
                                     isError = pesoError.isNotEmpty() && peso.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                             } else {
                                 Row(
                                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                     verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                 ) {
                                     OutlinedTextField(
                                         value = peso,
                                         onValueChange = { if (editandoPeso) peso = it },
                                         readOnly = !editandoPeso,
                                         label = { Text("Peso (kg)") },
                                         singleLine = true,
                                         isError = pesoError.isNotEmpty() && peso.isNotEmpty(),
                                         modifier = Modifier.weight(1f)
                                     )
                                     IconButton(onClick = { editandoPeso = !editandoPeso }) {
                                         Icon(
                                             imageVector = Icons.Default.Edit,
                                             contentDescription = "Editar",
                                             tint = if (editandoPeso) Color(0xFF183A6D) else Color.Gray
                                         )
                                     }
                                 }
                             }
                            
                            if (pesoError.isNotEmpty() && peso.isNotEmpty()) {
                                Text(pesoError, color = Color.Red, fontSize = 12.sp)
                            }
                            
                                                         // Altura - editable directamente si no hay consultas previas
                             if (ultimaConsulta == null) {
                OutlinedTextField(
                    value = altura,
                    onValueChange = { altura = it },
                    label = { Text("Altura (cm)") },
                    singleLine = true,
                                     isError = alturaError.isNotEmpty() && altura.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                             } else {
                                 Row(
                                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                     verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                 ) {
                                     OutlinedTextField(
                                         value = altura,
                                         onValueChange = { if (editandoAltura) altura = it },
                                         readOnly = !editandoAltura,
                                         label = { Text("Altura (cm)") },
                                         singleLine = true,
                                         isError = alturaError.isNotEmpty() && altura.isNotEmpty(),
                                         modifier = Modifier.weight(1f)
                                     )
                                     IconButton(onClick = { editandoAltura = !editandoAltura }) {
                                         Icon(
                                             imageVector = Icons.Default.Edit,
                                             contentDescription = "Editar",
                                             tint = if (editandoAltura) Color(0xFF183A6D) else Color.Gray
                                         )
                                     }
                                 }
                             }
                            
                            if (alturaError.isNotEmpty() && altura.isNotEmpty()) {
                                Text(alturaError, color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    }
                    2 -> {
                        // Tercera pestaña: Signos Vitales
                        Column {
                            Text("Signos Vitales:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                            
                            // Presión arterial
                OutlinedTextField(
                    value = presionArterial,
                    onValueChange = { presionArterial = it },
                    label = { Text("Presión arterial") },
                    singleLine = true,
                                isError = presionArterialError.isNotEmpty() && presionArterial.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                            if (presionArterialError.isNotEmpty() && presionArterial.isNotEmpty()) {
                                Text(presionArterialError, color = Color.Red, fontSize = 12.sp)
                            } else if (presionArterial.isEmpty()) {
                                Text("Formato: 120/80", color = Color.Gray, fontSize = 12.sp)
                            }
                            
                            // Temperatura
                OutlinedTextField(
                    value = temperatura,
                    onValueChange = { temperatura = it },
                    label = { Text("Temperatura (°C)") },
                    singleLine = true,
                                isError = temperaturaError.isNotEmpty() && temperatura.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                            if (temperaturaError.isNotEmpty() && temperatura.isNotEmpty()) {
                                Text(temperaturaError, color = Color.Red, fontSize = 12.sp)
                            } else if (temperatura.isEmpty()) {
                                Text("Temperatura entre 30 y 45°C", color = Color.Gray, fontSize = 12.sp)
                            }
                            
                            // Frecuencia cardíaca
                OutlinedTextField(
                    value = frecuenciaCardiaca,
                    onValueChange = { frecuenciaCardiaca = it },
                    label = { Text("Frecuencia cardíaca") },
                    singleLine = true,
                                isError = frecuenciaCardiacaError.isNotEmpty() && frecuenciaCardiaca.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                            if (frecuenciaCardiacaError.isNotEmpty() && frecuenciaCardiaca.isNotEmpty()) {
                                Text(frecuenciaCardiacaError, color = Color.Red, fontSize = 12.sp)
                            } else if (frecuenciaCardiaca.isEmpty()) {
                                Text("Frecuencia entre 40 y 200 bpm", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                    3 -> {
                        // Cuarta pestaña: Medicamentos
                        Column {
                            Text("Medicamentos:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                            
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
                                                            tint = if (medicamentosEditando[medicamento.nombre] == true) Color(0xFF183A6D) else Color.Gray
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
                        }
                    }
                }
                
                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red, fontSize = 12.sp)
                }
                
                // DatePicker para fecha de consulta
                if (showDatePickerConsulta) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePickerConsulta = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val safeMillis = millis + 12 * 60 * 60 * 1000 // Suma 12 horas para evitar desfase
                                    val localDate = Instant.ofEpochMilli(safeMillis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    fechaConsulta = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                }
                                showDatePickerConsulta = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePickerConsulta = false }) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                
                // DatePicker para fecha próxima consulta
                if (showDatePickerProxima) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePickerProxima = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val safeMillis = millis + 12 * 60 * 60 * 1000 // Suma 12 horas para evitar desfase
                                    val localDate = Instant.ofEpochMilli(safeMillis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    fechaProxima = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                }
                                showDatePickerProxima = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePickerProxima = false }) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Anterior (excepto en la primera sección)
                if (currentSection > 0) {
            Button(
                        onClick = { currentSection-- },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Anterior", color = Color.White)
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                // Botón Siguiente/Agregar Consulta
                Button(
                    onClick = {
                        when (currentSection) {
                            0 -> {
                                // Validar sección de información general
                                when {
                                    fechaConsultaError.isNotEmpty() || !ValidationUtils.isValidDate(fechaConsulta) -> {
                                        error = "Por favor corrige los errores en la fecha de consulta"
                                        return@Button
                                    }
                                    horaConsultaError.isNotEmpty() || !horaConsulta.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) -> {
                                        error = "Por favor corrige los errores en la hora de consulta"
                                        return@Button
                                    }
                                    motivoError.isNotEmpty() || !ValidationUtils.hasMinLength(motivo, 5) -> {
                                        error = "Por favor corrige los errores en el motivo"
                                        return@Button
                                    }
                                }
                                currentSection++
                                error = ""
                            }
                            1 -> {
                                // Validar sección de historial clínico
                                when {
                                    pesoError.isNotEmpty() || (peso.isNotEmpty() && !ValidationUtils.isValidWeight(peso)) -> {
                                        error = "Por favor corrige los errores en el peso"
                                        return@Button
                                    }
                                    alturaError.isNotEmpty() || (altura.isNotEmpty() && !ValidationUtils.isValidHeight(altura)) -> {
                                        error = "Por favor corrige los errores en la altura"
                                        return@Button
                                    }
                                }
                                currentSection++
                                error = ""
                            }
                            2 -> {
                                // Validar sección de signos vitales
                                when {
                                    presionArterialError.isNotEmpty() || (presionArterial.isNotEmpty() && !ValidationUtils.isValidBloodPressure(presionArterial)) -> {
                                        error = "Por favor corrige los errores en la presión arterial"
                                        return@Button
                                    }
                                    temperaturaError.isNotEmpty() || (temperatura.isNotEmpty() && !ValidationUtils.isValidTemperature(temperatura)) -> {
                                        error = "Por favor corrige los errores en la temperatura"
                                        return@Button
                                    }
                                    frecuenciaCardiacaError.isNotEmpty() || (frecuenciaCardiaca.isNotEmpty() && !ValidationUtils.isValidHeartRate(frecuenciaCardiaca)) -> {
                                        error = "Por favor corrige los errores en la frecuencia cardíaca"
                                        return@Button
                                    }
                                }
                                currentSection++
                                error = ""
                            }
                            3 -> {
                                // Última sección - Agregar consulta
                    // Convertir síntomas de texto a lista
                    val sintomas = if (sintomasTexto.isNotBlank()) {
                        sintomasTexto.split(",").map { it.trim() }
                    } else {
                        emptyList()
                    }
                    
                    val consulta = Consulta(
                        pacienteId = pacienteId,
                                    doctorId = doctorAsignado?.id ?: "",
                        fecha = fechaConsulta,
                        hora = horaConsulta,
                        motivo = motivo,
                        sintomas = sintomas,
                        duracionSintomas = duracionSintomas,
                        diagnosticoPrincipal = diagnosticoPrincipal,
                        diagnosticoSecundario = diagnosticoSecundario,
                        enfermedadesCronicas = enfermedadesCronicas,
                        alergias = alergias,
                        peso = peso.toDoubleOrNull() ?: 0.0,
                        altura = altura.toDoubleOrNull() ?: 0.0,
                        notas = notas,
                        estudios = estudios,
                        indicaciones = indicaciones,
                        requiereSeguimiento = requiereSeguimiento,
                        fechaProxima = fechaProxima,
                        presionArterial = presionArterial,
                        temperatura = temperatura,
                        frecuenciaCardiaca = frecuenciaCardiaca,
                        medicamentos = medicamentos
                    )
                    onConsultaAgregada(consulta)
                            }
                        }
                    },
                    enabled = when (currentSection) {
                        0 -> fechaConsultaError.isEmpty() && horaConsultaError.isEmpty() && motivoError.isEmpty() && 
                              fechaConsulta.isNotEmpty() && horaConsulta.isNotEmpty() && motivo.isNotEmpty()
                        1 -> pesoError.isEmpty() && alturaError.isEmpty()
                        2 -> presionArterialError.isEmpty() && temperaturaError.isEmpty() && frecuenciaCardiacaError.isEmpty()
                        3 -> true // Siempre habilitado en la última sección
                        else -> false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183A6D)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (currentSection == 3) "Agregar Consulta" else "Siguiente",
                        color = Color.White
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF183A6D))
            }
        }
    )
}
