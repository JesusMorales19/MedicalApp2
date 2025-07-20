package com.example.doctor.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctor.R

@Composable
fun DetalleCirugiaScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Detalle de Cirugía y Radiografía",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Imagen médica (izquierda)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Radiografía médica",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Información (derecha)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Resumen de la intervención",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Se realizó una artroscopía de rodilla izquierda con limpieza articular para tratar el dolor crónico. El procedimiento fue exitoso, sin complicaciones reportadas.",
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Detalles de la radiografía",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "La imagen radiográfica muestra reducción de inflamación postoperatoria, alineación adecuada de la articulación y ausencia de cuerpos extraños.",
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Fecha de procedimiento: 02/06/2025",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón "Ver más"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { /* TODO: Navegar a otra pantalla o mostrar más detalles */ },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver más")
            }
        }
    }
}
