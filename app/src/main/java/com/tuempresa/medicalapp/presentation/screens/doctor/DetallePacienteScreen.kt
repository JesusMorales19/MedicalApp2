package com.tuempresa.medicalapp.presentation.screens.doctor

import androidx.compose.foundation.Image
import com.tuempresa.medicalapp.presentation.screens.common.ServiciosMedicos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.medicalapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.tuempresa.medicalapp.data.models.Paciente
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.tuempresa.medicalapp.core.utils.ImageUtils
import com.tuempresa.medicalapp.core.utils.ProyeccionUtils
import com.tuempresa.medicalapp.data.models.PaqueteEnvio
import com.tuempresa.medicalapp.data.models.Doctor
import androidx.compose.ui.platform.LocalContext


@Composable
fun DetallePacienteScreen(
    paciente: Paciente? = null,
    consultas: List<com.tuempresa.medicalapp.data.models.Consulta> = emptyList(),
    cirugias: List<com.tuempresa.medicalapp.data.models.Cirugia> = emptyList(),
    radiografias: List<com.tuempresa.medicalapp.data.models.Radiografia> = emptyList(),
    nombreDoctor: String = "",
    apellidoDoctor: String = "",
    fotoDoctor: String = "",
    onServiciosClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val blue = Color(0xFF183A6D)
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            // Flecha de regreso
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), // baja la flecha
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(56.dp) // área clickeable más grande
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = blue,
                        modifier = Modifier.size(40.dp) // tamaño de la flecha
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Foto grande
            val imageBitmap = ImageUtils.base64ToImageBitmap(paciente?.fotoBase64 ?: "")
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Foto paciente",
                    modifier = Modifier
                        .size(400.dp)
                        .border(2.dp, Color(0xFFA4C8DF))
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
            Spacer(modifier = Modifier.height(30.dp))
            // Datos
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text("Nombre:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(paciente?.nombre ?: "No disponible", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(22.dp))
                Row {
                    Text("Apellido:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(paciente?.apellido ?: "No disponible", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(22.dp))
                Row {
                    Text("CURP:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(paciente?.curp ?: "No disponible", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(22.dp))
                Row {
                    Text("Tipo de sangre:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(paciente?.tipoSangre ?: "No disponible", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            // Botones
            Button(
                onClick = onServiciosClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue)
            ) {
                Text("Servicios medicos", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(65.dp))
            Button(
                onClick = {
                    if (paciente != null) {
                        val paquete = PaqueteEnvio(
                            paciente = paciente,
                            consultas = consultas,
                            cirugias = cirugias,
                            radiografias = emptyList(), // No enviar radiografías
                            doctor = Doctor() // Doctor vacío para cumplir con el modelo
                        )
                        ProyeccionUtils.enviarPaquete(context, paquete)
                    }
                    onHistorialClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue)
            ) {
                Text("Historial Medico", color = Color.White, fontSize = 16.sp)
            }
        }
    }
} 