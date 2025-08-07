package com.example.doctor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import com.example.doctor.R

@Composable
fun InformacionScreen(navController: androidx.navigation.NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // --- Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Foto redonda paciente
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "Foto paciente",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                // Nombre paciente
                Text(
                    text = "Ricardo Israel Gil Navarro",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            // Título procedimiento negritas, alineado derecha
            Text(
                text = "Artroscopía de rodilla izquierda",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Fila 1: Datos quirúrgicos y detalles clínicos ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Datos quirúrgicos (izquierda)
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Datos de la cirugía", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = "Cirugía")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nombre: Artroscopía de rodilla izquierda")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fecha: 10/11/2022")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalHospital, contentDescription = "Hospital")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hospital: Hospital Ángeles, CDMX")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Procedimiento")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Procedimiento: Mínimamente invasivo")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = "Recuperación")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recuperación: 4 semanas")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "Cirujano")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cirujano: Dr. Juan Méndez Ríos")
                    }
                }
            }

            // Detalles clínicos (derecha)
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detalles clínicos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Motivo", tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Motivo: Dolor persistente y bloqueo mecánico por lesión meniscal.")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = "Procedimiento")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Procedimiento: Limpieza articular y reparación parcial de menisco.")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Error, contentDescription = "Complicaciones")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complicaciones: Ninguna")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Pronóstico", tint = Color.Green)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pronóstico: Favorable. Paciente responde bien a fisioterapia.")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Fila 2: Tratamiento postoperatorio ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tratamiento postoperatorio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Medication, contentDescription = "Ketorolaco", tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ketorolaco 10 mg – 1 tableta cada 8h por 5 días")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Medication, contentDescription = "Paracetamol", tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Paracetamol 500 mg – 1 tableta diaria")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = "Fisioterapia", tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Fisioterapia – 2 veces por semana")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

// --- Botón para ir a DetalleCirugiaScreen ---
        Button(
            onClick = { navController.navigate("DetalleCirugia") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = "Ver Detalle", modifier = Modifier.padding(end = 4.dp))
            Text("Ver más detalles de la cirugía")
        }

    }
}
