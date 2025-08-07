package com.example.doctor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PendientesScreen(navController: NavController) {
    val tareasIniciales = listOf(
        "Revisar resultados de laboratorio",
        "Llamar a paciente Juan Pérez",
        "Actualizar expediente de Ana López",
        "Confirmar cirugía programada"
    )
    var tareas by remember { mutableStateOf(tareasIniciales.map { it to false }) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Columna de pendientes
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Lista de pendientes", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(tareas) { index, (tarea, completada) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tareas = tareas.toMutableList().also {
                                        it[index] = it[index].copy(second = !it[index].second)
                                    }
                                }
                        ) {
                            Icon(
                                imageVector = if (completada) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (completada) Color(0xFF66BB6A) else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = tarea,
                                fontSize = 14.sp,
                                color = if (completada) Color.Gray else Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Columna de calendario visual
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Calendario (ejemplo)", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                val dias = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                val fechas = (1..30).toList()

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    dias.forEach {
                        Text(
                            it,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Cambiar a List<List<Int?>> para aceptar nulls
                val semanas: List<List<Int?>> = fechas.chunked(7).map {
                    if (it.size < 7) it + List(7 - it.size) { null }
                    else it
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    semanas.forEach { semana ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            semana.forEach { dia ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(
                                            color = if (dia != null) Color(0xFFE3F2FD) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dia != null) {
                                        Text("$dia", fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
