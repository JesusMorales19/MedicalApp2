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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.medicalapp.core.utils.ImageUtils
import com.tuempresa.medicalapp.core.utils.ValidationUtils
import com.tuempresa.medicalapp.data.models.Doctor

@Composable
fun AddDoctorForm(
    onDismiss: () -> Unit,
    onDoctorAgregado: (Doctor) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var consultorio by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var cedulaProfesional by remember { mutableStateOf("") }
    var fotoBase64 by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var error by remember { mutableStateOf("") }
    
    // Estados para validaciones en tiempo real
    var nombreError by remember { mutableStateOf("") }
    var apellidoError by remember { mutableStateOf("") }
    var correoError by remember { mutableStateOf("") }
    var telefonoError by remember { mutableStateOf("") }
    var contrasenaError by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
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
    
    LaunchedEffect(correo) {
        correoError = when {
            correo.isEmpty() -> ""
            !ValidationUtils.isValidEmail(correo) -> "Formato de email inválido"
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
    
    LaunchedEffect(contrasena) {
        contrasenaError = ValidationUtils.getPasswordErrorMessage(contrasena)
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Doctor", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // Foto del doctor
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
                            contentDescription = "Foto del doctor",
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
                
                // Dropdown para especialidad mejorado
                var expandedEspecialidad by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    OutlinedTextField(
                        value = especialidad,
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
                        listOf("Cardiología", "Dermatología", "Endocrinología", "Gastroenterología", 
                               "Ginecología", "Neurología", "Oftalmología", "Ortopedia", 
                               "Pediatría", "Psiquiatría", "Radiología", "Traumatología").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    especialidad = option
                                    expandedEspecialidad = false
                                }
                            )
                        }
                    }
                }
                
                if (especialidad.isEmpty()) {
                    Text("Selecciona una especialidad", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Correo
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    isError = correoError.isNotEmpty() && correo.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (correoError.isNotEmpty() && correo.isNotEmpty()) {
                    Text(correoError, color = Color.Red, fontSize = 12.sp)
                } else if (correo.isEmpty()) {
                    Text("Ingresa un correo electrónico válido", color = Color.Gray, fontSize = 12.sp)
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
                
                // Contraseña con validación mejorada
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    isError = contrasenaError.isNotEmpty() && contrasena.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (contrasenaError.isNotEmpty() && contrasena.isNotEmpty()) {
                    Text(contrasenaError, color = Color.Red, fontSize = 12.sp)
                } else if (contrasena.isEmpty()) {
                    Text("La contraseña debe tener al menos 8 caracteres", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Consultorio
                OutlinedTextField(
                    value = consultorio,
                    onValueChange = { consultorio = it },
                    label = { Text("Consultorio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (consultorio.isEmpty()) {
                    Text("Ingresa el número o ubicación del consultorio", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Matrícula
                OutlinedTextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    label = { Text("Matrícula") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (matricula.isEmpty()) {
                    Text("Ingresa el número de matrícula", color = Color.Gray, fontSize = 12.sp)
                }
                
                // Cédula Profesional
                OutlinedTextField(
                    value = cedulaProfesional,
                    onValueChange = { cedulaProfesional = it },
                    label = { Text("Cédula Profesional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                if (cedulaProfesional.isEmpty()) {
                    Text("Ingresa el número de cédula profesional", color = Color.Gray, fontSize = 12.sp)
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
                        especialidad.isBlank() -> {
                            error = "Debes seleccionar una especialidad"
                            return@Button
                        }
                        correoError.isNotEmpty() || !ValidationUtils.isValidEmail(correo) -> {
                            error = "Por favor corrige los errores en el correo"
                            return@Button
                        }
                        telefonoError.isNotEmpty() || !ValidationUtils.isValidPhone(telefono) -> {
                            error = "Por favor corrige los errores en el teléfono"
                            return@Button
                        }
                        contrasenaError.isNotEmpty() || !ValidationUtils.isValidPassword(contrasena) -> {
                            error = "Por favor corrige los errores en la contraseña"
                            return@Button
                        }
                    }
                    
                    val doctor = Doctor(
                        nombre = nombre,
                        apellido = apellido,
                        especialidad = especialidad,
                        correo = correo,
                        telefono = telefono,
                        contrasena = contrasena,
                        consultorio = consultorio,
                        matricula = matricula,
                        cedulaProfesional = cedulaProfesional,
                        fotoBase64 = fotoBase64
                    )
                    onDoctorAgregado(doctor)
                },
                enabled = nombreError.isEmpty() && apellidoError.isEmpty() && correoError.isEmpty() && 
                         telefonoError.isEmpty() && contrasenaError.isEmpty() && 
                         nombre.isNotEmpty() && apellido.isNotEmpty() && especialidad.isNotEmpty() && 
                         correo.isNotEmpty() && telefono.isNotEmpty() && contrasena.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183A6D))
            ) {
                Text("Agregar Doctor", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF183A6D))
            }
        }
    )
} 