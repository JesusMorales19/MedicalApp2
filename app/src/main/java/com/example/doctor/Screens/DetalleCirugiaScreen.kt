package com.example.doctor.screens

import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DetalleCirugiaScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar simulado + nombre paciente
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Ricardo Israel Gil Navarro",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Nombre cirugía
            Text(
                text = "Artroscopía de rodilla izquierda",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Cuerpo dividido en 2 columnas y sección tratamiento abajo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Datos de la cirugía
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Datos de la cirugía",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SurgeryDataRow("Nombre:", "Artroscopía de rodilla izquierda")
                    SurgeryDataRow("Fecha:", "02/06/2025")
                    SurgeryDataRow("Hospital:", "Ángeles del Pedregal")
                    SurgeryDataRow("Tipo de procedimiento:", "Limpieza articular")
                    SurgeryDataRow("Recuperación:", "6 semanas")
                    SurgeryDataRow("Cirujano:", "Dr. Juan Pérez")
                }
            }

            // Detalles clínicos
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Detalles clínicos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SurgeryDataRow("Motivo:", "Dolor crónico")
                    SurgeryDataRow("Procedimiento:", "Limpieza articular")
                    SurgeryDataRow("Complicaciones:", "Ninguna")
                    SurgeryDataRow("Pronóstico:", "Favorable")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tratamiento postoperatorio (verde claro)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Tratamiento postoperatorio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Medicamentos:", fontWeight = FontWeight.SemiBold)
                Text(text = "• Ketorolaco 10mg – 1 tableta cada 8h por 5 días")
                Text(text = "• Paracetamol 500mg – 1 tableta diaria")

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Terapias:", fontWeight = FontWeight.SemiBold)
                Text(text = "• Fisioterapia – 2 veces por semana")
            }
        }
    }
}

@Composable
fun SurgeryDataRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MedicalServices,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            modifier = Modifier.width(160.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}
