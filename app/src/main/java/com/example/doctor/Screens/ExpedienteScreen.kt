package com.example.doctor.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.doctor.navigation.Screen

@Composable
fun ExpedienteScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Parte 1: Datos generales del paciente ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "Foto paciente",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text("Ana María López Pérez", fontWeight = FontWeight.Bold)
                Text("Edad: 32 años")
                Text("Sexo: Femenino")
                Text("Tipo de sangre: O+")
            }

            Spacer(modifier = Modifier.weight(1f))

            Text("Última consulta\n05/06/2025", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Datos Generales Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Datos Generales", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Fecha de nacimiento: 01/01/1991")
                Text("CURP: LOPA910101HDFRRS09")
                Text("Tel: +52 55 1234 5678")
                Text("Email: ana.lopez@example.com")
                Text("Dirección: Calle Falsa 123, CDMX")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Parte 2: Detalle de Cirugía ---

        Text("Detalle de Cirugía", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Datos Cirugía
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Datos de la cirugía", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nombre: Artroscopía de rodilla izquierda")
                    Text("Fecha: 02/06/2025")
                    Text("Hospital: Ángeles del Pedregal")
                    Text("Procedimiento: Limpieza articular")
                    Text("Recuperación: 6 semanas")
                    Text("Cirujano: Dr. Juan Pérez")
                }
            }

            // Detalles Clínicos
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detalles clínicos", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Motivo: Dolor crónico")
                    Text("Procedimiento realizado: Limpieza articular")
                    Text("Complicaciones: Ninguna")
                    Text("Pronóstico: Favorable")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Parte 3: Tratamiento ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tratamiento postoperatorio", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Medicamentos:")
                Text("• Ketorolaco 10mg – 1 tableta cada 8h por 5 días")
                Text("• Paracetamol 500mg – 1 tableta diaria")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Terapias:")
                Text("• Fisioterapia – 2 veces por semana")
            }
        }
    }
}
