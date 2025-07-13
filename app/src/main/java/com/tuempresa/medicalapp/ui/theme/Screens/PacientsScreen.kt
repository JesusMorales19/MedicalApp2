package com.tuempresa.medicalapp.ui.theme.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.tuempresa.medicalapp.R
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.tuempresa.medicalapp.ui.theme.Navegacion.BottomNavigationBar
import com.tuempresa.medicalapp.ui.theme.Navegacion.PantallaActual
import androidx.compose.foundation.layout.navigationBarsPadding

data class Paciente(
    val nombre: String,
    val fechaRegistro: String,
    val diagnostico: String,
    val foto: Int // resource id
)


// Modifica PacientesScreen para aceptar un Modifier
@Composable
fun PacientesScreen(
    modifier: Modifier = Modifier,
    onPacienteClick: (String) -> Unit = {}
) {
    val pacientes = listOf(
        Paciente("Ricardo Israel", "Sep 05, 2021", "Diagnóstico", R.drawable.ic_persona),
        Paciente("Maria Morales", "Sep 05, 2022", "Diagnóstico", R.drawable.ic_persona),
        Paciente("Cesar Montes", "Sep 05, 2023", "Diagnóstico", R.drawable.ic_persona),
        Paciente("Josefa Perez", "Sep 05, 2024", "Diagnóstico", R.drawable.ic_persona),
        Paciente("Cristina Gonzales", "Sep 05, 2025", "Diagnóstico", R.drawable.ic_persona),
    )
    var search by remember { mutableStateOf("") }
    var blue = Color(0xFF183A6D)
    val lightGray = Color(0xFFF5F7FA)

    // Filtrar pacientes por nombre o fecha de registro
    val pacientesFiltrados = pacientes.filter {
        it.nombre.contains(search, ignoreCase = true) ||
        it.fechaRegistro.contains(search, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .background(lightGray)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        // Barra de búsqueda
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Buscar...", color = Color(0xFFA4C8DF), fontSize = 18.sp) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Buscar",
                    tint = Color(0xFFA4C8DF)
                )
            },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = blue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )
        Spacer(modifier = Modifier.height(26.dp))
        // Título
        Text(
            "Mis Pacientes",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = blue,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align (Alignment.CenterHorizontally)
        )
        // Lista de pacientes filtrada
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(pacientesFiltrados) { paciente ->
                PacienteCard(
                    paciente = paciente,
                    onDiagnosticoClick = { onPacienteClick(paciente.nombre) }
                )
            }
        }
    }
}

@Composable
fun PacienteCard(
    paciente: Paciente,
    onDiagnosticoClick: () -> Unit
) {
    val blue = Color(0xFF183A6D)
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = paciente.foto),
                    contentDescription = "Foto paciente",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFA4C8DF), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        paciente.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = blue
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit_pacient),
                    contentDescription = "Editar Paciente",
                    tint = blue,
                    modifier = Modifier.size(33.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Fecha alineada a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Fecha",
                    tint = blue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Fecha de Registro: ",
                    fontSize = 17.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    paciente.fechaRegistro,
                    fontSize = 16.sp,
                    color = blue,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Diagnóstico centrado abajo y como enlace
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_diagnostico),
                    contentDescription = "Diagnóstico",
                    tint = blue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    paciente.diagnostico,
                    fontSize = 17.sp,
                    color = blue,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onDiagnosticoClick() }
                )
            }
        }
    }
}
