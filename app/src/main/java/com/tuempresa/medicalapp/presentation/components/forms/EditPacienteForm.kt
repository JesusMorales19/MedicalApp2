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
import androidx.compose.material3.*
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
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.medicalapp.core.utils.ImageUtils
import com.tuempresa.medicalapp.data.models.Paciente

@Composable
fun EditPacienteForm(
    paciente: Paciente,
    onDismiss: () -> Unit,
    onPacienteEditado: (Paciente) -> Unit
) {
    var telefono by remember { mutableStateOf(paciente.telefono) }
    var email by remember { mutableStateOf(paciente.email) }
    var direccion by remember { mutableStateOf(paciente.direccion) }
    var fotoBase64 by remember { mutableStateOf(paciente.fotoBase64) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var error by remember { mutableStateOf("") }
    
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
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Paciente", fontWeight = FontWeight.Bold) },
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
                    } else if (fotoBase64.isNotBlank()) {
                        val bitmap = ImageUtils.decodeBase64ToBitmap(fotoBase64)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Foto del paciente",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
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
                
                // Campos de solo lectura
                OutlinedTextField(
                    value = paciente.nombre,
                    onValueChange = {},
                    label = { Text("Nombre") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = paciente.apellido,
                    onValueChange = {},
                    label = { Text("Apellido") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = paciente.curp,
                    onValueChange = {},
                    label = { Text("CURP") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = paciente.fechaNacimiento,
                    onValueChange = {},
                    label = { Text("Fecha de nacimiento") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = paciente.sexo,
                    onValueChange = {},
                    label = { Text("Sexo") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = paciente.tipoSangre,
                    onValueChange = {},
                    label = { Text("Tipo de sangre") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                // Campos editables
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    singleLine = true,
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
                    if (telefono.isBlank() || email.isBlank() || direccion.isBlank()) {
                        error = "Teléfono, email y dirección son obligatorios"
                        return@Button
                    }
                    
                    val pacienteEditado = paciente.copy(
                        telefono = telefono,
                        email = email,
                        direccion = direccion,
                        fotoBase64 = fotoBase64
                    )
                    onPacienteEditado(pacienteEditado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183A6D))
            ) {
                Text("Guardar Cambios", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF183A6D))
            }
        }
    )
} 