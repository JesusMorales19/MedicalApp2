package com.example.doctor.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import com.example.doctor.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    var currentTime by remember { mutableStateOf(getFormattedTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedTime()
            delay(1000L)
        }
    }

    val alphaAnim = rememberInfiniteTransition()
    val alpha by alphaAnim.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(32.dp)
    ) {
        // Izquierda: fondo azul oscuro con texto blanco
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFF1565C0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Imagen\nMédica\nAquí",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(16.dp),
                lineHeight = 30.sp
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Derecha: fondo claro con texto oscuro
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFFFFFFFF))
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido a Consulta Extendida",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.alpha(alpha)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¿En qué podemos ayudarte, Dr. Ricardo Israel Gil Navarro?",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF424242)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentTime,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF616161)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    HomeOptionCard(
                        title = "Pacientes",
                        icon = Icons.Default.Person,
                        background = Color(0xFF90CAF9),
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate(Screen.Pacientes.name) }

                    HomeOptionCard(
                        title = "Expediente",
                        icon = Icons.Default.Folder,
                        background = Color(0xFFA5D6A7),
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate(Screen.Expediente.name) }

                    HomeOptionCard(
                        title = "Cirugía",
                        icon = Icons.Default.LocalHospital,
                        background = Color(0xFFCE93D8),
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate(Screen.Cirugia.name) }
                }
            }
        }
    }
}

@Composable
fun HomeOptionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1.3f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

private fun getFormattedTime(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
