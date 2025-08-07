package com.tuempresa.medicalapp.presentation.navigation.Navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


enum class PantallaActual {
    PACIENTES, PERFIL
}

@Composable
fun BottomNavigationBar(
    pantallaActual: PantallaActual,
    onPantallaSeleccionada: (PantallaActual) -> Unit
) {
    val blue = Color(0xFF183A6D)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFFF5F7FA))
            .navigationBarsPadding(), // ← Esto es lo que hace que la barra se suba justo arriba de la barra del sistema
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPantallaSeleccionada(PantallaActual.PACIENTES) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Pacientes",
                tint = if (pantallaActual == PantallaActual.PACIENTES) blue else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(
            onClick = { onPantallaSeleccionada(PantallaActual.PERFIL) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configuración",
                tint = if (pantallaActual == PantallaActual.PERFIL) blue else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
