package com.example.doctor.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctor.R

@Composable
fun ExpedienteScreen(navController: NavController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Encabezado superior
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "Foto paciente",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Ana María López Pérez", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Edad: 52 años")
                    Text("Sexo: Femenino")
                    Text("Tipo de sangre: O+")
                }

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = "Última consulta\n05/06/2025",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fila 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Datos Generales
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Datos Generales", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("📅 Fecha de nacimiento: 01/01/1973")
                        Text("🆔 CURP: LOPA730101HDFRRS09")
                        Text("📞 Tel: +52 55 1234 5678")
                        Text("✉️ Email: ana.lopez@example.com")
                        Text("🏠 Dirección: Calle Falsa 123, CDMX")
                    }
                }

                // Historial Médico
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Historial Médico", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Diagnóstico actual: Hipertensión")
                        Text("Enfermedad crónica desde 2015")
                        Text("Cirugías previas: Histerectomía (2010)")
                        Text("Alergias: Ninguna")
                        Text("Peso: 71 kg")
                        Text("Altura: 1.61 m")
                        Text("IMC: 27.4")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Medicación Actual
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Medicación Actual", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Losartán 50 mg – 1 tableta diaria")
                        Text("• Ácido Fólico – 1 tableta diaria")
                    }
                }

                // Últimas Consultas
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFB3E5FC)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Últimas Consultas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• 05/06/2025: Se mantiene estable")
                        Text("• 08/05/2025: Elevación de presión, ajustar dieta")
                        Text("• 12/04/2025: Presión normal")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notas del doctor
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Notas del Doctor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("“Paciente con buena respuesta al tratamiento. Seguir indicaciones nutricionales y actividad física moderada. Cita en un mes.”")
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate("informacion") // 👈 Navega a InformacionScreen
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a siguiente"
            )
        }
    }
}
