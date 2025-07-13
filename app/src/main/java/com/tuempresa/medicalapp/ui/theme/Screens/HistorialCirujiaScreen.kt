package com.tuempresa.medicalapp.ui.theme.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.tuempresa.medicalapp.R
import androidx.compose.ui.draw.shadow
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Modelo de datos
data class Surgery(
    val type: String,
    val date: String,
    val hospital: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurgeryHistoryScreen(
    onBack: () -> Unit = {},
    onSurgeryClick: (Surgery) -> Unit = {}
) {
    val blue = Color(0xFF183A6D)
    val surgeries = listOf(
        Surgery(type = "Apendicectomía", date = "15/03/2014", hospital = "IMSS Clínica 23"),
        Surgery(type = "Colecistectomía", date = "22/08/2019", hospital = "Hospital General Ángeles"),
        Surgery(type = "Hernioplastia inguinal", date = "05/11/2021", hospital = "Clínica San Felipe")
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()

    // Filtrar cirugías por fecha seleccionada
    val filteredSurgeries = if (selectedDate.isNotEmpty()) {
        surgeries.filter { it.date == selectedDate }
    } else {
        surgeries
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Botón de regreso igual que en otras screens
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = blue,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Título con ícono
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_cirujia),
                    contentDescription = "Icono Cirugía",
                    modifier = Modifier.size(32.dp), // Puedes ajustar el tamaño aquí también
                    tint = blue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Historial de Cirugías",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp, // Más pequeño que antes
                    color = blue
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Filtro de fecha visual y funcional, centrado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFDDEBFA)),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .height(48.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar_1),
                    contentDescription = "Filtrar por fecha",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedDate.isNotEmpty()) selectedDate else "Filtrar por fecha",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = blue
                )
            }
        }
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val safeMillis = millis + 12 * 60 * 60 * 1000 // Suma 12 horas para evitar desfase
                            val localDate = Instant.ofEpochMilli(safeMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            selectedDate = formattedDate
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Recuadro grande para las cards
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE6F0FA))
                .padding(12.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredSurgeries) { surgery ->
                    SurgeryCard(surgery = surgery, onClick = { onSurgeryClick(surgery) })
                }
            }
        }
    }
}

@Composable
fun SurgeryCard(surgery: Surgery, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_carpeta),
                        contentDescription = "Tipo de cirugía",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cirugía: ${surgery.type}", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_1),
                        contentDescription = "Fecha de cirugía",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Fecha: ${surgery.date}", fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_hospital),
                        contentDescription = "Hospital",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hospital: ${surgery.hospital}", fontWeight = FontWeight.Medium)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver más",
                tint = Color(0xFF183A6D),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}