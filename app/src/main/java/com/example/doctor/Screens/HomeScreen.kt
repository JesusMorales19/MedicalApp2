package com.example.doctor.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import com.example.doctor.navigation.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doctor.Model.TVDataViewModel
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
@Composable
fun HomeScreen(navController: NavController, tvDataViewModel: TVDataViewModel) {
    var currentTime by remember { mutableStateOf(getFormattedTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedTime()
            delay(1000L)
        }
    }

    val doctorData = tvDataViewModel.doctorData

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
        // Izquierda: fondo azul oscuro con foto y datos del doctor
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFF1565C0)),
            contentAlignment = Alignment.Center
        ) {
            // Foto del doctor - ahora ocupa todo el fondo azul
            if (doctorData?.foto != null) {
                val bitmap = try {
                    val imageBytes = Base64.decode(doctorData.foto, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                } catch (e: Exception) {
                    null
                }
                
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto del doctor",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Icono por defecto si hay error en la foto
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = Color.White
                        )
                    }
                }
            } else {
                // Icono por defecto si no hay foto
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Derecha: fondo claro con opciones
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
                text = if (doctorData != null) {
                    "Bienvenido Dr(a). ${doctorData.nombre} ${doctorData.apellido}"
                } else {
                    "Bienvenido Dr(a). Ricardo Israel Gil Navarro"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.alpha(alpha),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¿En qué podemos ayudarte?",
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
                    ) { navController.navigate(Screen.Expediente.name) }

                    HomeOptionCard(
                        title = "Pendientes",
                        icon = Icons.Default.CheckCircle,
                        background = Color(0xFFFFF59D),
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate(Screen.Pendientes.name) }

                    HomeOptionCard(
                        title = "Signos Vitales",
                        icon = Icons.Default.Favorite,
                        background = Color(0xFFA5D6A7),
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate(Screen.SignosVitales.name) }
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