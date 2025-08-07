package com.tuempresa.medicalapp.presentation.components.forms

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.medicalapp.core.utils.ImageUtils
import com.tuempresa.medicalapp.data.models.Paciente
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Period
import kotlin.text.Regex
import com.tuempresa.medicalapp.core.utils.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPacienteForm(
    onDismiss: () -> Unit,
    onPacienteAgregado: (Paciente) -> Unit,
    listaDoctores: List<com.tuempresa.medicalapp.data.models.Doctor> = emptyList()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var curp by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var tipoSangre by remember { mutableStateOf("") }
    var idDoctorAsignado by remember { mutableStateOf("") }
    var fotoBase64 by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    
    // Estados para validaciones en tiempo real
    var nombreError by remember { mutableStateOf("") }
    var apellidoError by remember { mutableStateOf("") }
    var curpError by remember { mutableStateOf("") }
    var fechaNacimientoError by remember { mutableStateOf("") }
    var telefonoError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var direccionError by remember { mutableStateOf("") }
    
    // Estados para dropdowns
    var expandedSexo by remember { mutableStateOf(false) }
    var expandedTipoSangre by remember { mutableStateOf(false) }
    var expandedEspecialidad by remember { mutableStateOf(false) }
    var expandedDoctor by remember { mutableStateOf(false) }
    
    // Estados para especialidad y doctor
    var especialidadSeleccionada by remember { mutableStateOf("") }
    var mostrarCampoDoctor by remember { mutableStateOf(false) }
    
    // Estado para selector de fecha
    var showDatePicker by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    // Selector de imagen
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            fotoBase64 = ImageUtils.compressAndConvertToBase64(context, it)
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            showSettingsDialog = true
        }
    }
    
    // Validaciones en tiempo real
    LaunchedEffect(nombre) {
        nombreError = when {
            nombre.isEmpty() -> ""
            !ValidationUtils.isOnlyLetters(nombre) -> "El nombre solo puede contener letras"
            !ValidationUtils.hasMinLength(nombre, 2) -> "El nombre debe tener al menos 2 caracteres"
            else -> ""
        }
    }
    
    LaunchedEffect(apellido) {
        apellidoError = when {
            apellido.isEmpty() -> ""
            !ValidationUtils.isOnlyLetters(apellido) -> "El apellido solo puede contener letras"
            !ValidationUtils.hasMinLength(apellido, 2) -> "El apellido debe tener al menos 2 caracteres"
            else -> ""
        }
    }
    
    LaunchedEffect(curp) {
        curpError = when {
            curp.isEmpty() -> ""
            !ValidationUtils.isValidCURP(curp) -> "Formato de CURP inválido"
            else -> ""
        }
    }
    
    LaunchedEffect(fechaNacimiento) {
        fechaNacimientoError = when {
            fechaNacimiento.isEmpty() -> ""
            !ValidationUtils.isValidDate(fechaNacimiento) -> "Formato de fecha inválido (DD/MM/YYYY)"
            else -> ""
        }
    }
    
    LaunchedEffect(telefono) {
        telefonoError = when {
            telefono.isEmpty() -> ""
            !ValidationUtils.isValidPhone(telefono) -> "El teléfono debe tener 10 dígitos"
            else -> ""
        }
    }
    
    LaunchedEffect(email) {
        emailError = when {
            email.isEmpty() -> ""
            !ValidationUtils.isValidEmail(email) -> "Formato de email inválido"
            else -> ""
        }
    }
    
    LaunchedEffect(direccion) {
        direccionError = when {
            direccion.isEmpty() -> ""
            !ValidationUtils.hasMinLength(direccion, 5) -> "La dirección debe tener al menos 5 caracteres"
            else -> ""
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Paciente", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // Foto del paciente
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable {
                            when {
                                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED -> {
                                    launcher.launch("image/*")
                                }
                                else -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                }
                            }
                        }
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Foto del paciente",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Agregar foto",
                            modifier = Modifier.size(50.dp),
                            tint = Color.Gray
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar foto",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    isError = nombreError.isNotEmpty() && nombre.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (nombreError.isNotEmpty() && nombre.isNotEmpty()) {
                    Text(nombreError, color = Color.Red, fontSize = 12.sp)
                } else if (nombre.isEmpty()) {
                    Text("El nombre debe tener al menos 2 caracteres", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Apellido
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    singleLine = true,
                    isError = apellidoError.isNotEmpty() && apellido.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (apellidoError.isNotEmpty() && apellido.isNotEmpty()) {
                    Text(apellidoError, color = Color.Red, fontSize = 12.sp)
                } else if (apellido.isEmpty()) {
                    Text("El apellido debe tener al menos 2 caracteres", color = Color.Gray, fontSize = 12.sp)
                }
                
                // CURP
                OutlinedTextField(
                    value = curp,
                    onValueChange = { curp = it.uppercase() },
                    label = { Text("CURP") },
                    singleLine = true,
                    isError = curpError.isNotEmpty() && curp.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (curpError.isNotEmpty() && curp.isNotEmpty()) {
                    Text(curpError, color = Color.Red, fontSize = 12.sp)
                } else if (curp.isEmpty()) {
                    Text("Ingresa un CURP válido", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Selector de fecha de nacimiento mejorado como en radiografías
                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de nacimiento") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    },
                    isError = fechaNacimientoError.isNotEmpty() && fechaNacimiento.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { showDatePicker = true }
                )
                
                if (fechaNacimientoError.isNotEmpty() && fechaNacimiento.isNotEmpty()) {
                    Text(fechaNacimientoError, color = Color.Red, fontSize = 12.sp)
                } else if (fechaNacimiento.isEmpty()) {
                    Text("Selecciona la fecha de nacimiento", color = Color.Gray, fontSize = 12.sp)
                }
                
                // DatePicker real para fecha de nacimiento
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
                                    fechaNacimiento = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
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
                
                // Dropdown para sexo mejorado
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    OutlinedTextField(
                        value = sexo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sexo") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedSexo) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir",
                                modifier = Modifier.clickable { expandedSexo = !expandedSexo }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedSexo = !expandedSexo }
                    )
                    
                    DropdownMenu(
                        expanded = expandedSexo,
                        onDismissRequest = { expandedSexo = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Masculino", "Femenino").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    sexo = option
                                    expandedSexo = false
                                }
                            )
                        }
                    }
                }
                
                if (sexo.isEmpty()) {
                    Text("Selecciona el sexo", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Teléfono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    isError = telefonoError.isNotEmpty() && telefono.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (telefonoError.isNotEmpty() && telefono.isNotEmpty()) {
                    Text(telefonoError, color = Color.Red, fontSize = 12.sp)
                } else if (telefono.isEmpty()) {
                    Text("El teléfono debe tener 10 dígitos", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    isError = emailError.isNotEmpty() && email.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (emailError.isNotEmpty() && email.isNotEmpty()) {
                    Text(emailError, color = Color.Red, fontSize = 12.sp)
                } else if (email.isEmpty()) {
                    Text("Ingresa un email válido", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    singleLine = true,
                    isError = direccionError.isNotEmpty() && direccion.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (direccionError.isNotEmpty() && direccion.isNotEmpty()) {
                    Text(direccionError, color = Color.Red, fontSize = 12.sp)
                } else if (direccion.isEmpty()) {
                    Text("La dirección debe tener al menos 5 caracteres", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Dropdown para tipo de sangre mejorado
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    OutlinedTextField(
                        value = tipoSangre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de sangre") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedTipoSangre) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir",
                                modifier = Modifier.clickable { expandedTipoSangre = !expandedTipoSangre }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedTipoSangre = !expandedTipoSangre }
                    )
                    
                    DropdownMenu(
                        expanded = expandedTipoSangre,
                        onDismissRequest = { expandedTipoSangre = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    tipoSangre = option
                                    expandedTipoSangre = false
                                }
                            )
                        }
                    }
                }
                
                if (tipoSangre.isEmpty()) {
                    Text("Selecciona el tipo de sangre", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Dropdown para especialidad
                if (listaDoctores.isNotEmpty()) {
                    val especialidades = listaDoctores
                        .filter { it.activo && it.rol == "doctor" } // Solo doctores activos, no secretarias
                        .map { it.especialidad }
                        .distinct()
                        .sorted()
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        OutlinedTextField(
                            value = especialidadSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Especialidad") },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedEspecialidad) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expandir",
                                    modifier = Modifier.clickable { expandedEspecialidad = !expandedEspecialidad }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedEspecialidad = !expandedEspecialidad }
                        )
                        
                        DropdownMenu(
                            expanded = expandedEspecialidad,
                            onDismissRequest = { expandedEspecialidad = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            especialidades.forEach { especialidad ->
                                DropdownMenuItem(
                                    text = { Text(especialidad) },
                                    onClick = {
                                        especialidadSeleccionada = especialidad
                                        mostrarCampoDoctor = true
                                        idDoctorAsignado = "" // Resetear doctor seleccionado
                                        expandedEspecialidad = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Dropdown para doctor asignado (solo se muestra si se seleccionó especialidad)
                if (mostrarCampoDoctor && especialidadSeleccionada.isNotEmpty()) {
                    val doctoresDeEspecialidad = listaDoctores.filter { 
                        it.especialidad == especialidadSeleccionada && it.activo && it.rol == "doctor"
                    }
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        OutlinedTextField(
                            value = listaDoctores.find { it.id == idDoctorAsignado }?.let { "${it.nombre} ${it.apellido}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Doctor asignado") },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedDoctor) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expandir",
                                    modifier = Modifier.clickable { expandedDoctor = !expandedDoctor }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedDoctor = !expandedDoctor }
                        )
                        
                        DropdownMenu(
                            expanded = expandedDoctor,
                            onDismissRequest = { expandedDoctor = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            doctoresDeEspecialidad.forEach { doctor ->
                                DropdownMenuItem(
                                    text = { Text("${doctor.nombre} ${doctor.apellido}") },
                                    onClick = {
                                        idDoctorAsignado = doctor.id
                                        expandedDoctor = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validaciones finales
                    when {
                        nombreError.isNotEmpty() || !ValidationUtils.isNotEmpty(nombre) -> {
                            error = "Por favor corrige los errores en el nombre"
                            return@Button
                        }
                        apellidoError.isNotEmpty() || !ValidationUtils.isNotEmpty(apellido) -> {
                            error = "Por favor corrige los errores en el apellido"
                            return@Button
                        }
                        curpError.isNotEmpty() || !ValidationUtils.isValidCURP(curp) -> {
                            error = "Por favor corrige los errores en el CURP"
                            return@Button
                        }
                        fechaNacimientoError.isNotEmpty() || !ValidationUtils.isValidDate(fechaNacimiento) -> {
                            error = "Por favor corrige los errores en la fecha de nacimiento"
                            return@Button
                        }
                        sexo.isBlank() -> {
                            error = "Debes seleccionar el sexo"
                            return@Button
                        }
                        telefonoError.isNotEmpty() || !ValidationUtils.isValidPhone(telefono) -> {
                            error = "Por favor corrige los errores en el teléfono"
                            return@Button
                        }
                        emailError.isNotEmpty() || !ValidationUtils.isValidEmail(email) -> {
                            error = "Por favor corrige los errores en el email"
                            return@Button
                        }
                        direccionError.isNotEmpty() || !ValidationUtils.hasMinLength(direccion, 5) -> {
                            error = "Por favor corrige los errores en la dirección"
                            return@Button
                        }
                        tipoSangre.isBlank() -> {
                            error = "Debes seleccionar el tipo de sangre"
                            return@Button
                        }
                        especialidadSeleccionada.isBlank() -> {
                            error = "Debes seleccionar una especialidad"
                            return@Button
                        }
                        idDoctorAsignado.isBlank() -> {
                            error = "Debes seleccionar un doctor"
                            return@Button
                        }
                    }
                    
                    // Calcular edad
                    val edad = try {
                        val fechaNac = LocalDate.parse(fechaNacimiento, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        val hoy = LocalDate.now()
                        Period.between(fechaNac, hoy).years
                    } catch (e: Exception) {
                        0
                    }
                    
                    // Fecha de creación
                    val fechaCreacion = System.currentTimeMillis()
                    val fechaCreacionStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    
                    val paciente = Paciente(
                        nombre = nombre,
                        apellido = apellido,
                        curp = curp,
                        fechaNacimiento = fechaNacimiento,
                        edad = edad,
                        sexo = sexo,
                        telefono = telefono,
                        email = email,
                        direccion = direccion,
                        tipoSangre = tipoSangre,
                        idDoctorAsignado = idDoctorAsignado,
                        fotoBase64 = fotoBase64,
                        fechaCreacion = fechaCreacion,
                        fechaCreacionStr = fechaCreacionStr
                    )
                    onPacienteAgregado(paciente)
                },
                enabled = nombreError.isEmpty() && apellidoError.isEmpty() && curpError.isEmpty() && 
                         fechaNacimientoError.isEmpty() && telefonoError.isEmpty() && emailError.isEmpty() && 
                         direccionError.isEmpty() && nombre.isNotEmpty() && apellido.isNotEmpty() && 
                         curp.isNotEmpty() && fechaNacimiento.isNotEmpty() && sexo.isNotEmpty() && 
                         telefono.isNotEmpty() && email.isNotEmpty() && direccion.isNotEmpty() && tipoSangre.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183A6D))
            ) {
                Text("Agregar Paciente", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF183A6D))
            }
        }
    )
} 