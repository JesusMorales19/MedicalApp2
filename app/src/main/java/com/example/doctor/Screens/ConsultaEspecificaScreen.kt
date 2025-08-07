package com.example.doctor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.foundation.clickable

@Composable
fun ConsultaEspecificaScreen(
    navController: NavController,
    consultaData: ConsultaEspecificaData? = null
) {
    // Datos de ejemplo si no se proporcionan
    val data = consultaData ?: ConsultaEspecificaData(
        tipo = "consultaEspecifica",
        paciente = PacienteConsulta(
            id = "paciente_123",
            nombre = "María",
            apellido = "García López",
            foto = null,
            tipoSangre = "O+",
            sexo = "Femenino"
        ),
        doctor = DoctorConsulta(
            id = "doctor_456",
            nombre = "Dr. Carlos",
            apellido = "Rodríguez"
        ),
        consulta = DetalleConsulta(
            id = "consulta_789",
            fecha = "15/12/2024",
            hora = "10:30 AM",
            motivo = "Dolor de cabeza persistente",
            duracionSintomas = "3 días",
            diagnosticoPrincipal = "Migraña tensional",
            diagnosticoSecundario = "Estrés",
            enfermedadesCronicas = "Hipertensión",
            alergias = "Penicilina",
            peso = 65.5,
            altura = 1.65,
            notas = "Paciente refiere dolor intenso en la región temporal bilateral",
            estudios = "Tomografía de cráneo",
            indicaciones = "Reposo, evitar luces brillantes",
            requiereSeguimiento = true,
            fechaProxima = "22/12/2024",
            presionArterial = "120/80 mmHg",
            temperatura = "36.8°C",
            frecuenciaCardiaca = "72 lpm",
            sintomas = listOf("Dolor de cabeza", "Náuseas", "Fotofobia"),
            medicamentos = listOf(
                MedicamentoConsulta("Ibuprofeno", "400mg", "Cada 8 horas"),
                MedicamentoConsulta("Paracetamol", "500mg", "Cada 6 horas"),
                MedicamentoConsulta("Sumatriptán", "50mg", "Al inicio del dolor")
            )
        )
    )

    var currentPage by remember { mutableStateOf(0) }

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
            
            Column {
                Text(
                    text = "${data.paciente.nombre} ${data.paciente.apellido}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "Consulta del ${data.consulta.fecha} - ${data.consulta.hora}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Contenido principal con navegación por flechas
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentPage) {
                0 -> ConsultaPage1(data)
                1 -> ConsultaPage2(data)
            }
            
            // Flechas de navegación
            if (data.consulta.medicamentos.isNotEmpty() || 
                data.consulta.indicaciones.isNotEmpty() || 
                data.consulta.estudios.isNotEmpty() || 
                data.consulta.notas.isNotEmpty() ||
                data.consulta.requiereSeguimiento) {
                
                // Flecha izquierda (Anterior)
                if (currentPage > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, bottom = 24.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { currentPage-- },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Anterior",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
                
                // Flecha derecha (Siguiente)
                if (currentPage < 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 24.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { currentPage++ },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Siguiente",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsultaPage1(data: ConsultaEspecificaData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Contenido principal en dos columnas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Columna izquierda - Datos de la consulta
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Título con icono
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Datos de la consulta",
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
                            InfoRow("Motivo", data.consulta.motivo)
                            InfoRow("Duración de los síntomas", "${data.consulta.duracionSintomas} días")
                            InfoRow("Fecha", data.consulta.fecha)
                        }
                        
                        // Columna derecha
                        Column(modifier = Modifier.weight(1f)) {
                            InfoRow("Hora", data.consulta.hora)
                            InfoRow("Tipo sangre", data.paciente.tipoSangre)
                            InfoRow("Sexo", data.paciente.sexo)
                        }
                    }
                    
                    // Nombre del doctor centrado
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Médico responsable",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${data.doctor.nombre} ${data.doctor.apellido}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                }
            }

            // Columna derecha - Diagnósticos
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Título con icono
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Diagnósticos",
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
                            InfoRow("Diagnóstico principal", data.consulta.diagnosticoPrincipal)
                            InfoRow("Enfermedades crónicas", data.consulta.enfermedadesCronicas)
                        }
                        
                        // Columna derecha
                        Column(modifier = Modifier.weight(1f)) {
                            InfoRow("Diagnóstico secundario", data.consulta.diagnosticoSecundario)
                            InfoRow("Alergias", data.consulta.alergias)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sección de signos vitales
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Título con icono
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Signos vitales",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }
                
                // Signos vitales en dos columnas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Columna izquierda
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRowWithUnit("Presión arterial", data.consulta.presionArterial, "mmHg")
                        InfoRowWithUnit("Peso", data.consulta.peso.toString(), "kg")
                    }
                    
                    // Columna derecha
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRowWithUnit("Temperatura", data.consulta.temperatura, "°C")
                        InfoRowWithUnit("Altura", data.consulta.altura.toString(), "m")
                    }
                    
                    // Columna adicional
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRowWithUnit("Frecuencia cardíaca", data.consulta.frecuenciaCardiaca, "lpm")
                        InfoRow("IMC", "${String.format("%.1f", data.consulta.peso / (data.consulta.altura * data.consulta.altura))}")
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsultaPage2(data: ConsultaEspecificaData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // Contenido principal en dos columnas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Columna izquierda - Síntomas y notas
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Título con icono
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Información clínica",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                    
                    // Síntomas
                    Column {
                        Text(
                            text = "Síntomas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        data.consulta.sintomas.forEach { sintoma ->
                            Text(
                                text = "• $sintoma",
                                fontSize = 14.sp,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Notas
                    InfoRow("Notas", data.consulta.notas)
                }
            }

            // Columna derecha - Estudios y seguimiento
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Título con icono
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Estudios y seguimiento",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                    
                    // Estudios
                    InfoRow("Estudios solicitados", data.consulta.estudios)
                    
                    // Seguimiento
                    InfoRow("Requiere seguimiento", if (data.consulta.requiereSeguimiento) "Sí" else "No")
                    if (data.consulta.requiereSeguimiento) {
                        InfoRow("Próxima cita", data.consulta.fechaProxima)
                    }
                    
                    // Indicaciones
                    InfoRow("Indicaciones", data.consulta.indicaciones)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sección de medicamentos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                        text = "Medicamentos recetados",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }
                
                // Medicamentos con scroll
                LazyColumn(
                    modifier = Modifier.height(100.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(data.consulta.medicamentos) { medicamento ->
                        MedicamentoItem(medicamento)
                    }
                }
            }
        }
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
        verticalArrangement = Arrangement.spacedBy(2.dp)
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
private fun InfoRowWithUnit(label: String, value: String, unit: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B7280)
        )
        Text(
            text = "$value $unit",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937),
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun MedicamentoItem(medicamento: MedicamentoConsulta) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
            Text(
                "${medicamento.dosis} ${medicamento.frecuencia}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}