package com.example.doctor.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctor.R
import androidx.compose.foundation.BorderStroke
import com.example.doctor.Model.ExpedienteData
import com.example.doctor.Model.Medicamento
import com.example.doctor.Model.Consulta
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doctor.Model.TVDataViewModel
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.doctor.screens.RadiografiasModal


@Composable
fun ExpedienteScreen(navController: NavHostController, tvDataViewModel: TVDataViewModel) {
    // Estado para forzar la recomposición cuando lleguen nuevos datos
    var ultimaActualizacion by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // Observar cambios en los datos del paciente
    val pacientesJson = tvDataViewModel.pacientesJson
    val mensajeRecibido = tvDataViewModel.mensajeRecibido
    
    // Forzar actualización cuando lleguen nuevos datos
    LaunchedEffect(pacientesJson, mensajeRecibido) {
        if (pacientesJson != null && mensajeRecibido == "datosPaciente") {
            ultimaActualizacion = System.currentTimeMillis()
            Log.d("ExpedienteScreen", "Datos actualizados - Nueva actualización: $ultimaActualizacion")
        }
    }
    
    val logString = pacientesJson?.toString() ?: "Sin datos recibidos"
    Log.d("ExpedienteScreen", "JSONArray recibido: $pacientesJson")
    val expedienteData: ExpedienteData? = pacientesJson?.let { arr ->
        if (arr.length() > 0) {
            val obj = arr.getJSONObject(0)
            val paciente = obj.getJSONObject("paciente")
            val ultimaConsulta = obj.getJSONObject("ultimaConsulta")
            val consultasAnteriores = obj.getJSONArray("consultasAnteriores")
            
            // Procesar medicamentos de la última consulta
            val medicamentosJson = ultimaConsulta.optJSONArray("medicamentos") ?: org.json.JSONArray()
            val medicamentos = List(medicamentosJson.length()) { i ->
                val med = medicamentosJson.getJSONObject(i)
                Medicamento(
                    nombre = med.optString("nombre", ""),
                    dosis = med.optString("dosis", ""),
                    frecuencia = med.optString("frecuencia", "")
                )
            }
            
            // Procesar consultas anteriores
            val consultas = mutableListOf<Consulta>()
            // Agregar la última consulta como primera
            consultas.add(Consulta(
                fecha = ultimaConsulta.optString("fecha", ""),
                motivo = ultimaConsulta.optString("motivo", "")
            ))
            // Agregar las consultas anteriores
            for (i in 0 until consultasAnteriores.length()) {
                val cons = consultasAnteriores.getJSONObject(i)
                consultas.add(Consulta(
                    fecha = cons.optString("fecha", ""),
                    motivo = cons.optString("motivo", "")
                ))
            }
            
            val pesoStr = ultimaConsulta.optString("peso", "0").replace(",", ".")
            val alturaStr = ultimaConsulta.optString("altura", "0").replace(",", ".")
            val peso = pesoStr.toFloatOrNull() ?: 0f
            val altura = alturaStr.toFloatOrNull() ?: 0f
            val imc = if (peso > 0 && altura > 0) String.format("%.1f", peso / (altura * altura)) else "0.0"
            
            ExpedienteData(
                nombreCompleto = paciente.optString("nombre", "") + " " + paciente.optString("apellido", ""),
                edad = paciente.optString("edad", ""),
                sexo = paciente.optString("sexo", ""),
                tipoSangre = paciente.optString("tipoSangre", ""),
                fotoBase64 = paciente.optString("foto", ""),
                fechaNacimiento = paciente.optString("fechaNacimiento", ""),
                telefono = paciente.optString("telefono", ""),
                direccion = paciente.optString("direccion", ""),
                curp = paciente.optString("CURP", ""),
                email = paciente.optString("email", ""),
                ultimaConsultaFecha = ultimaConsulta.optString("fecha", ""),
                diagnosticoActual = ultimaConsulta.optString("diagnosticoPrincipal", ""),
                alergias = ultimaConsulta.optString("alergias", ""),
                enfermedadesCronicas = ultimaConsulta.optString("enfermedadesCronicas", ""),
                cirugiasPrevias = ultimaConsulta.optString("cirugiasPrevias", ""),
                peso = ultimaConsulta.optString("peso", ""),
                altura = ultimaConsulta.optString("altura", ""),
                imc = imc,
                medicamentos = medicamentos,
                consultas = consultas,
                notasDoctor = ultimaConsulta.optString("notas", "")
            )
        } else null
    }

    val data = expedienteData ?: ExpedienteData(
        nombreCompleto = "Jesus Octavio Morales Hernández",
        edad = "21",
        sexo = "Masculino",
        tipoSangre = "O+",
        fotoBase64 = null,
        fechaNacimiento = "2004-05-16",
        telefono = "6182988791",
        direccion = "Durango",
        curp = "CURP167391083910",
        email = "morales.jesus1868@gmail.com",
        ultimaConsultaFecha = "28/07/2025",
        diagnosticoActual = "Hipertensión",
        alergias = "Ácido fólico",
        enfermedadesCronicas = "Ninguna",
        cirugiasPrevias = "No especificado",
        peso = "70.4",
        altura = "1.7",
        imc = "24.4",
        medicamentos = listOf(
            Medicamento("Ibuprofeno", "1 Tableta", "cada 8 horas")
        ),
        consultas = listOf(),
        notasDoctor = ""
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(20.dp)
        ) {
            // Log de actualización en consola
            if (pacientesJson != null && mensajeRecibido == "datosPaciente") {
                val tiempoTranscurrido = if (tvDataViewModel.ultimaActualizacion > 0) {
                    val ahora = System.currentTimeMillis()
                    val diferencia = ahora - tvDataViewModel.ultimaActualizacion
                    when {
                        diferencia < 1000 -> "hace un momento"
                        diferencia < 60000 -> "hace ${diferencia / 1000} segundos"
                        else -> "hace ${diferencia / 60000} minutos"
                    }
                } else {
                    "hace un momento"
                }
                
                val numeroConsultas = pacientesJson.let { arr ->
                    if (arr.length() > 0) {
                        val obj = arr.getJSONObject(0)
                        val consultasAnteriores = obj.getJSONArray("consultasAnteriores")
                        consultasAnteriores.length() + 1 // +1 por la última consulta
                    } else 0
                }
                
                val numeroMedicamentos = pacientesJson.let { arr ->
                    if (arr.length() > 0) {
                        val obj = arr.getJSONObject(0)
                        val ultimaConsulta = obj.getJSONObject("ultimaConsulta")
                        val medicamentosJson = ultimaConsulta.optJSONArray("medicamentos") ?: org.json.JSONArray()
                        medicamentosJson.length()
                    } else 0
                }
                
                Log.d("ExpedienteScreen", "🔄 DATOS ACTUALIZADOS EN TIEMPO REAL")
                Log.d("ExpedienteScreen", "⏰ Última actualización: $tiempoTranscurrido")
                Log.d("ExpedienteScreen", "📊 Estadísticas del paciente:")
                Log.d("ExpedienteScreen", "   • Consultas: $numeroConsultas")
                Log.d("ExpedienteScreen", "   • Medicamentos: $numeroMedicamentos")
                Log.d("ExpedienteScreen", "✅ Interfaz actualizada automáticamente")
            }
            
            // Log de consulta específica
            if (tvDataViewModel.mostrarConsultaEspecifica && tvDataViewModel.consultaEspecificaData != null) {
                val consulta = tvDataViewModel.consultaEspecificaData!!
                Log.d("ExpedienteScreen", "🏥 CONSULTA ESPECÍFICA RECIBIDA")
                Log.d("ExpedienteScreen", "👤 Paciente: ${consulta.paciente.nombre} ${consulta.paciente.apellido}")
                Log.d("ExpedienteScreen", "📅 Fecha: ${consulta.consulta.fecha} - ${consulta.consulta.hora}")
                Log.d("ExpedienteScreen", "💊 Medicamentos: ${consulta.consulta.medicamentos.size}")
                Log.d("ExpedienteScreen", "📋 Modal de consulta específica abierto")
            }
            
            // Header principal más compacto
            HeaderSection(data)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Primera fila: Datos Generales e Historial Médico (más pequeñas)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Datos Generales (más pequeña)
                Column(modifier = Modifier.weight(1f)) {
                    DatosGeneralesCard(data)
                }
                
                // Historial Médico (más pequeña)
                Column(modifier = Modifier.weight(1f)) {
                    HistorialMedicoCard(data)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Segunda fila: Medicación y Consultas (más grandes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Medicación Actual (más grande)
                Column(modifier = Modifier.weight(1f)) {
                    MedicacionActualCard(data.medicamentos)
                }
                
                // Últimas consultas (más grande)
                Column(modifier = Modifier.weight(1f)) {
                    UltimasConsultasCard(data.consultas)
                }
            }
        }
        
        // Modal de radiografías
        if (tvDataViewModel.mostrarRadiografias && tvDataViewModel.radiografiaData != null) {
            RadiografiasModal(
                radiografiaData = tvDataViewModel.radiografiaData!!,
                onClose = { tvDataViewModel.cerrarRadiografias() }
            )
        }
        

    }
}

@Composable
fun HeaderSection(data: ExpedienteData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto real del paciente
            if (!data.fotoBase64.isNullOrEmpty()) {
                val bitmap = try {
                    val imageBytes = Base64.decode(data.fotoBase64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                } catch (e: Exception) {
                    null
                }
                
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto del paciente",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // Si hay error, mostrar icono por defecto
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            tint = Color(0xFF1976D2)
                        )
                    }
                }
            } else {
                // Icono por defecto si no hay foto
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = Color(0xFF1976D2)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información del paciente
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    data.nombreCompleto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "${data.edad} años",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            data.sexo,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            data.tipoSangre,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Última consulta
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "Última consulta",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
                Text(
                    data.ultimaConsultaFecha,
                    color = Color(0xFF1976D2),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DatosGeneralesCard(data: ExpedienteData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Datos Generales",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Campos organizados en dos columnas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Columna izquierda
                Column(modifier = Modifier.weight(1f)) {
                    InfoRow("Fecha de nacimiento", data.fechaNacimiento, Icons.Default.CalendarToday)
                    InfoRow("Teléfono", data.telefono, Icons.Default.Phone)
                    InfoRow("CURP", data.curp, Icons.Default.Badge)
                }
                
                // Columna derecha
                Column(modifier = Modifier.weight(1f)) {
                    InfoRow("Email", data.email, Icons.Default.Email)
                    InfoRow("Dirección", data.direccion, Icons.Default.LocationOn)
                }
            }
        }
    }
}

@Composable
fun MedicacionActualCard(medicamentos: List<Medicamento>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Medication,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Medicación Actual",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (medicamentos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(medicamentos) { medicamento ->
                        MedicamentoItem(medicamento)
                    }
                }
            } else {
                Text(
                    "Sin medicamentos actuales",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun NotasDoctorCard(notas: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.NoteAlt,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Notas del doctor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    notas,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun HistorialMedicoCard(data: ExpedienteData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Historial Médico",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Campos organizados en dos columnas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Columna izquierda
                Column(modifier = Modifier.weight(1f)) {
                    InfoRow("Diagnóstico Actual", data.diagnosticoActual, Icons.Default.MedicalServices)
                    InfoRow("Enfermedades crónicas", data.enfermedadesCronicas, Icons.Default.Warning)
                }
                
                // Columna derecha
                Column(modifier = Modifier.weight(1f)) {
                    InfoRow("Alergias", data.alergias, Icons.Default.Info)
                    InfoRow("Cirugías Previas", data.cirugiasPrevias, Icons.Default.LocalHospital)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Medidas físicas más pequeñas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    MedidaCard("Peso", "${data.peso} kg", Icons.Default.Scale, Color(0xFF4CAF50))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MedidaCard("Altura", "${data.altura} m", Icons.Default.Straighten, Color(0xFF4CAF50))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MedidaCard("IMC", data.imc, Icons.Default.Favorite, Color(0xFF9C27B0))
                }
            }
        }
    }
}

@Composable
fun UltimasConsultasCard(consultas: List<Consulta>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Últimas consultas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (consultas.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(consultas) { consulta ->
                        ConsultaItem(consulta.fecha, consulta.motivo)
                    }
                }
            } else {
                Text(
                    "Sin consultas previas",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                value.ifEmpty { "No especificado" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun MedicamentoItem(medicamento: Medicamento) {
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
            Text(
                "${medicamento.dosis} ${medicamento.frecuencia}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun MedidaCard(label: String, valor: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                label,
                fontSize = 10.sp,
                color = Color.Gray
            )
            Text(
                valor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ConsultaItem(fecha: String, descripcion: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                fecha,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            Text(
                descripcion,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}