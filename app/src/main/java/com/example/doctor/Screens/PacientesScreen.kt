package com.example.doctor.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctor.navigation.Screen  // ✅ Asegúrate de importar esto

data class Paciente(
    val nombre: String,
    val edad: Int,
    val sexo: String,
    val tipoSangre: String
)

@Composable
fun PacientesScreen(navController: NavController) {
    val pacientes = listOf(
        Paciente("Ana María López Pérez", 42, "Femenino", "O+"),
        Paciente("José Carlos Mendoza", 51, "Masculino", "A-"),
        Paciente("Laura González Ramírez", 36, "Femenino", "B+"),
        Paciente("Pedro Hernández Silva", 63, "Masculino", "AB-")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lista de Pacientes",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(pacientes) { paciente ->
                PacienteCard(paciente = paciente) {
                    // ✅ Navega a la pantalla de Expediente
                    navController.navigate(Screen.Expediente.name)
                }
            }
        }
    }
}

@Composable
fun PacienteCard(paciente: Paciente, onVerMasClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = paciente.nombre,
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Edad: ${paciente.edad} años", fontSize = 16.sp)
            Text("Sexo: ${paciente.sexo}", fontSize = 16.sp)
            Text("Tipo de sangre: ${paciente.tipoSangre}", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVerMasClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver más información", color = Color.White)
            }
        }
    }
}
