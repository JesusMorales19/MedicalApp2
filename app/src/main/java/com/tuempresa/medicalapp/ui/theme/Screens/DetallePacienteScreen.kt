package com.tuempresa.medicalapp.ui.theme.Screens

import androidx.compose.foundation.Image
import com.tuempresa.medicalapp.ui.theme.Screens.ServiciosMedicos
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

@Composable
fun DetallePacienteScreen(
    nombre: String = "Ricardo Israel",
    apellido: String = "Gil Navarro",
    tipoSangre: String = "O+",
    foto: Int = R.drawable.ic_persona, // Cambia por el recurso real
    onServiciosClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val blue = Color(0xFF183A6D)
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
            Image(
                painter = painterResource(id = foto),
                contentDescription = "Foto paciente",
                modifier = Modifier
                    .size(400.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(30.dp))
            // Datos
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text("Nombre:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(nombre, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(22.dp))
                Row {
                    Text("Apellido:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(apellido, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(22.dp))
                Row {
                    Text("Tipo de sangre:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tipoSangre, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                onClick = onHistorialClick,
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