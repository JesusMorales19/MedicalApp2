package com.tuempresa.medicalapp.ui.theme.Screens

import android.net.Uri
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextDecoration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.medicalapp.R
import android.util.Patterns

@Composable
fun PerfilDoctorScreen() {
    // Variables separadas
    var nombre by remember { mutableStateOf("Ricardo Israel") }
    var apellido by remember { mutableStateOf("Gil Navarro") }
    var correo by remember { mutableStateOf("Ricardo.gil@gmail.com") }
    var telefono by remember { mutableStateOf("6181562324") }
    var especialidad by remember { mutableStateOf("Cardiologia") }
    var contrasena by remember { mutableStateOf("12345678") }
    var editando by remember { mutableStateOf(false) }
    var correoError by remember { mutableStateOf("") }
    var contrasenaError by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
        }
    }
    val blue = Color(0xFF183A6D)
    val lightGray = Color(0xFFF5F7FA)

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
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
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
            if (editando) {
                IconButton(onClick = { launcher.launch("image/*") }) {
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
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Nombre completo
        Text(
            text = "Dr. $nombre $apellido",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Spacer(modifier = Modifier.height(60.dp))
        // CORREO
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
                        color = blue // O Color.Black
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
        Spacer(modifier = Modifier.height(20.dp))

        // CONTRASEÑA (igual que ya tienes)

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
            if (editando) {
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = {
                        contrasena = it
                        contrasenaError = ""
                    },
                    isError = contrasenaError.isNotEmpty(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .width(300.dp) // Ajusta el ancho según tu diseño
                        .height(60.dp), // Opcional: para que no se vea tan alto
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )
            } else {
                Text(
                    "*".repeat(contrasena.length),
                    fontSize = 16.sp,
                    color = blue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (contrasenaError.isNotEmpty()) {
            Text(contrasenaError, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(20.dp))

        // ESPECIALIDAD
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
            if (editando) {
                OutlinedTextField(
                    value = especialidad,
                    onValueChange = { especialidad = it },
                    singleLine = true,
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        color = blue // O Color.Black
                    )
                )
            } else {
                Text(
                    especialidad,
                    fontSize = 16.sp,
                    color = blue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Botón editar/guardar
        Button(
            onClick = {
                if (editando) {
                    // Validaciones
                    var valido = true
                    if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                        correoError = "Correo inválido"
                        valido = false
                    }
                    if (contrasena.length < 6) {
                        contrasenaError = "Mínimo 6 caracteres"
                        valido = false
                    }
                    if (valido) {
                        editando = false
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
    }
}
