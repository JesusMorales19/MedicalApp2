package com.example.doctor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.doctor.R

@Composable
fun SignosVitalesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // 🔹 Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Icono de paciente",
                    tint = Color.Gray,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Ana María López Pérez", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cake, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("52 años", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.Female, contentDescription = null, tint = Color.Magenta)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Femenino", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.Bloodtype, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("O+", fontSize = 14.sp)
                    }
                }
            }

            Text(
                "Última consulta - 05/06/2025",
                fontSize = 14.sp,
                color = Color(0xFF1976D2), // Azul
                modifier = Modifier.align(Alignment.Top)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Fila 1
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            // Datos de la Consulta
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF1976D2))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Datos de la Consulta", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fecha: 05/06/2025")
                    Text("Hora: 10:30 a.m.")
                    Text("Duración: 25 minutos")
                    Text("Tipo: Seguimiento hipertensión")
                    Text("Médico: Dra. Gabriela Ruiz Martínez")
                    Text("Ubicación: Consultorio 2. Clínica Familiar Durango")
                }
            }

            // Signos Vitales
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF388E3C))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Signos Vitales", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Presión arterial: 122/78 mmHg", color = Color(0xFF2E7D32))
                    Text("FC: 72 lpm", color = Color(0xFF1976D2))
                    Text("FR: 16 rpm", color = Color(0xFF9575CD))
                    Text("Temperatura: 36.7°C", color = Color(0xFFFFB74D))
                    Text("Peso: 71 kg")
                    Text("Altura: 1.61 m")
                    Text("IMC: 27.4", color = Color(0xFFFFF176))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Fila 2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            // Motivo y síntomas
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Motivo de consulta", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Motivo principal: Revisión rutinaria para hipertensión")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Síntomas reportados:")
                    Text("- Ligera fatiga matutina")
                    Text("- Mareo leve ocasional")
                    Text("- Sin cefaleas ni visión borrosa")
                }
            }

            // Diagnóstico
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF43A047))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Diagnóstico", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Principal: Hipertensión controlada",
                        modifier = Modifier
                            .background(Color(0xFFC8E6C9))
                            .padding(6.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Secundario: Sobrepeso grado I",
                        modifier = Modifier
                            .background(Color(0xFFFFF59D))
                            .padding(6.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Evaluación:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFBBDEFB))
                            .padding(6.dp)
                    )
                    Text(
                        "La paciente ha respondido positivamente al tratamiento. No se observan complicaciones cardiovasculares ni progresión."
                    )
                }
            }
        }
    }
}
