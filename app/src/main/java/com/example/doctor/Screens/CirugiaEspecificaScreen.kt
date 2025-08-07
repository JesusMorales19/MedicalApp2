package com.example.doctor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctor.Model.*
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun CirugiaEspecificaScreen(
    navController: NavController,
    cirugiaData: CirugiaEspecificaData? = null
) {
    // Datos de ejemplo si no se proporcionan
    val data = cirugiaData ?: CirugiaEspecificaData(
        tipo = "cirugiaEspecifica",
        paciente = com.example.doctor.Model.PacienteCirugia(
            id = "paciente_123",
            nombre = "Ricardo Israel",
            apellido = "Gil Navarro",
            foto = null
        ),
        doctor = com.example.doctor.Model.DoctorCirugia(
            id = "doctor_456",
            nombre = "Dr. Juan",
            apellido = "Méndez Ríos",
            especialidad = "Cirugía General",
            foto = null
        ),
        cirugia = com.example.doctor.Model.DetalleCirugia(
            id = "cirugia_789",
            nombreCirugia = "Artroscopía de rodilla izquierda",
            fecha = "10/11/2022",
            nombreDoctor = "Dr. Juan Méndez Ríos",
            tipoProcesamiento = "Mínimamente invasivo",
            tiempoRecuperacion = "4 semanas",
            motivo = "Dolor persistente y bloqueo mecánico por lesión meniscal.",
            procedimiento = "Se realizó limpieza articular y reparación parcial de menisco.",
            pronostico = "Favorable. Paciente responde bien a fisioterapia.",
            complicaciones = "Ninguna",
            hospital = "Hospital Ángeles, CDMX",
            quirofano = "Quirófano 3",
            medicamentos = listOf(
                com.example.doctor.Model.MedicamentoCirugia("Ketorolaco 10 mg", "1 tableta", "Cada 8h por 5 días"),
                com.example.doctor.Model.MedicamentoCirugia("Paracetamol 500 mg", "1 tableta", "Diaria"),
                com.example.doctor.Model.MedicamentoCirugia("Fisioterapia", "", "2 veces por semana")
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Información del paciente - Header más pequeño
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto del paciente
            PatientPhoto(data.paciente.foto, data.paciente.nombre, data.paciente.apellido)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "${data.paciente.nombre} ${data.paciente.apellido}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Contenido principal en dos columnas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Columna izquierda - Datos de la cirugía
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Título con icono
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Datos de la cirugía",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                    
                    // Dos columnas internas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Columna izquierda
                        Column(modifier = Modifier.weight(1f)) {
                            InfoRow("Nombre de la cirugía", data.cirugia.nombreCirugia)
                            InfoRow("Fecha", data.cirugia.fecha)
                            InfoRow("Hospital", data.cirugia.hospital)
                        }
                        
                        // Columna derecha
                        Column(modifier = Modifier.weight(1f)) {
                            InfoRow("Quirófano", data.cirugia.quirofano)
                            InfoRow("Tipo de procedimiento", data.cirugia.tipoProcesamiento)
                            InfoRow("Tiempo de recuperación", data.cirugia.tiempoRecuperacion)
                        }
                    }
                    
                    // Nombre del doctor centrado
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cirujano responsable",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data.cirugia.nombreDoctor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                }
            }

            // Columna derecha - Detalles clínicos
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título con icono
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Detalles clínicos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                    
                    // Dos columnas internas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Columna izquierda
                        Column(modifier = Modifier.weight(1f)) {
                            InfoRow("Complicaciones", data.cirugia.complicaciones)
                            InfoRow("Pronóstico", data.cirugia.pronostico)
                        }
                        
                        // Columna derecha
                        Column(modifier = Modifier.weight(1f)) {
                            InfoRow("Motivo", data.cirugia.motivo)
                            InfoRow("Procedimiento", data.cirugia.procedimiento)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de tratamiento postoperatorio
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Título con icono
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tratamiento postoperatorio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }
                
                // Medicamentos con diseño de ExpedienteScreen y scroll
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.cirugia.medicamentos) { medicamento ->
                        MedicamentoItem(medicamento)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PatientPhoto(fotoBase64: String?, nombre: String, apellido: String) {
    Card(
        modifier = Modifier.size(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (fotoBase64 != null && fotoBase64.isNotEmpty()) {
            val bitmap = decodeBase64Image(fotoBase64)
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto del paciente",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                PlaceholderAvatar(nombre, apellido)
            }
        } else {
            PlaceholderAvatar(nombre, apellido)
        }
    }
}

private fun decodeBase64Image(fotoBase64: String): android.graphics.Bitmap? {
    return try {
        val cleanBase64 = fotoBase64.replace("data:image/jpeg;base64,", "")
        val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun PlaceholderAvatar(nombre: String, apellido: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3B82F6)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${nombre[0]}${apellido[0]}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937),
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun MedicamentoItem(medicamento: MedicamentoCirugia) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(32.dp)
                .background(Color(0xFF4CAF50), RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                medicamento.nombre,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            if (medicamento.dosis.isNotEmpty()) {
                Text(
                    "${medicamento.dosis} ${medicamento.frecuencia}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            } else {
                Text(
                    medicamento.frecuencia,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
} 