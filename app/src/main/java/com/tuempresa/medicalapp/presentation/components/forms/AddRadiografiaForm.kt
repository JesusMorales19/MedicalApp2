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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import com.tuempresa.medicalapp.data.models.Doctor
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.models.Radiografia
import com.tuempresa.medicalapp.core.utils.ImageUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRadiografiaForm(
    paciente: Paciente,
    doctor: Doctor?,
    onDismiss: () -> Unit,
    onRadiografiaSubida: (Radiografia) -> Unit
) {
    var tipo by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var imagenBase64 by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val tipos = listOf("Tórax", "Extremidades", "Columna")
    val context = LocalContext.current

    // Imagen picker
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagenBase64 = ImageUtils.compressAndConvertToBase64(context, uri)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Subir Radiografía", fontWeight = FontWeight.Bold, color = Color(0xFF183A6D)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("ID: (automático)")
                Text("Paciente: ${paciente.id}")
                Text("Médico: ${doctor?.nombre ?: ""} ${doctor?.apellido ?: ""}")
                Spacer(Modifier.height(8.dp))
                // Tipo
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de radiografía") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                    )
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tipos.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    tipo = opcion
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                // Fecha
                OutlinedTextField(
                    value = fecha,
                    onValueChange = {},
                    label = { Text("Fecha de la radiografía") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showDatePicker = true }
                )
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
                // Imagen
                Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(if (imagenBase64.isEmpty()) "Seleccionar imagen" else "Imagen seleccionada")
                }
                // Observaciones
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = { Text("Observaciones") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tipo.isBlank() || fecha.isBlank() || imagenBase64.isBlank()) {
                        error = "Todos los campos son obligatorios"
                        return@Button
                    }
                    onRadiografiaSubida(
                        Radiografia(
                            id = "",
                            pacienteId = paciente.id,
                            medicoId = doctor?.id ?: "",
                            tipo = tipo,
                            fecha = fecha,
                            imagenBase64 = imagenBase64,
                            observaciones = observaciones
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183A6D))
            ) {
                Text("Subir Radiografía", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF183A6D))
            }
        }
    )
} 