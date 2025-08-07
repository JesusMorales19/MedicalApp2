package com.tuempresa.medicalapp.presentation.screens.secretary

import android.net.Uri
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.medicalapp.R
import com.tuempresa.medicalapp.presentation.viewmodels.AuthViewModel
import com.tuempresa.medicalapp.core.utils.ImageUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asImageBitmap
import android.Manifest
import android.os.Build.VERSION_CODES
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.content.Intent
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PerfilSecretariaScreen(
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()
    val secretaria = uiState.doctor

    var correo by remember { mutableStateOf(secretaria?.correo ?: "") }
    var telefono by remember { mutableStateOf(secretaria?.telefono ?: "") }
    var editando by remember { mutableStateOf(false) }
    var correoError by remember { mutableStateOf("") }
    var camposError by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            Log.d("PERFIL_SECRETARIA", "URI seleccionada: $uri")
            imageUri = uri
        } else {
            Log.e("PERFIL_SECRETARIA", "No se seleccionó imagen o URI inválida")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            showSettingsDialog = true
        }
    }

    val blue = Color(0xFF183A6D)
    val lightGray = Color(0xFFF5F7FA)

    LaunchedEffect(editando) {
        if (editando) {
            correo = secretaria?.correo ?: ""
            telefono = secretaria?.telefono ?: ""
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                Button(onClick = {
                    showLogoutDialog = false
                    authViewModel.cerrarSesion()
                }) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Permiso requerido") },
            text = { Text("Debes habilitar el permiso de almacenamiento en Ajustes para seleccionar una imagen.") },
            confirmButton = {
                Button(onClick = {
                    showSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }) {
                    Text("Ir a Ajustes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGray)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(42.dp))
        // Foto de perfil
        Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.BottomEnd) {
            when {
                imageUri != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(400.dp)
                            .clip(CircleShape)
                    )
                }
                secretaria?.fotoBase64?.isNotEmpty() == true -> {
                    val bitmap = ImageUtils.decodeBase64ToBitmap(secretaria?.fotoBase64 ?: "")
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(400.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_persona),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(400.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                !secretaria?.fotoUrl.isNullOrEmpty() -> {
                    Image(
                        painter = rememberAsyncImagePainter(secretaria?.fotoUrl),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(400.dp)
                            .clip(CircleShape)
                    )
                }
                else -> {
                    Image(
                        painter = painterResource(id = R.drawable.ic_persona),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(400.dp)
                            .clip(CircleShape)
                    )
                }
            }
            if (editando) {
                IconButton(onClick = {
                    val permission = if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    val permissionStatus = ContextCompat.checkSelfPermission(context, permission)
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        launcher.launch("image/*")
                    } else {
                        permissionLauncher.launch(permission)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_pacient),
                        contentDescription = "Editar foto",
                        tint = blue,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape)
                    )
                        }
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
            text = { Text("Información actualizada exitosamente") },
            confirmButton = {
                Button(
                    onClick = { showSuccessAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("OK", color = Color.LightGray)
                }
            }
        )
    }
}
        Spacer(modifier = Modifier.height(20.dp))
        // Nombre completo (solo visible)
        Text(
            text = "Secretaria: ${secretaria?.nombre ?: ""} ${secretaria?.apellido ?: ""}",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Correo (editable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Correo:",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            if (editando) {
                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        correoError = ""
                    },
                    isError = correoError.isNotEmpty(),
                    singleLine = true,
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        color = blue
                    )
                )
            } else {
                Text(
                    correo,
                    fontSize = 16.sp,
                    color = blue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (correoError.isNotEmpty()) {
            Text(correoError, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Teléfono (editable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Celular:",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            if (editando) {
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    singleLine = true,
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        color = blue
                    )
                )
            } else {
                Text(
                    telefono,
                    fontSize = 16.sp,
                    color = blue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Especialidad (solo visible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Especialidad:",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Text(
                secretaria?.especialidad ?: "",
                fontSize = 16.sp,
                color = blue,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Contraseña (solo visible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Contraseña:",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Text(
                "********",
                fontSize = 16.sp,
                color = blue,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (camposError.isNotEmpty()) {
            Text(
                text = camposError,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Button(
            onClick = {
                if (editando) {
                    var valido = true
                    camposError = ""
                    if (correo.isBlank() || telefono.isBlank()) {
                        camposError = "Correo y celular son obligatorios."
                        valido = false
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                        correoError = "Correo inválido"
                        valido = false
                    }
                    if (valido && secretaria != null) {
                        var base64Image: String? = null
                        if (imageUri != null) {
                            // Convertir imagen a Base64
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
                            ImageDecoder.decodeBitmap(source) { decoder, info, source ->
                                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                                decoder.isMutableRequired = true
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri!!)
                        }
                        base64Image = bitmap?.let { ImageUtils.compressAndConvertToBase64(it) } ?: ""
                        }
                        authViewModel.actualizarDoctorSoloCamposBase64(secretaria.id, correo, telefono, base64Image)
                        showSuccessAlert = true
                        editando = false
                        imageUri = null
                    }
                } else {
                    editando = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = blue),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                if (editando) "Guardar" else "Editar Información",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { showLogoutDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                "Cerrar Sesión",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
