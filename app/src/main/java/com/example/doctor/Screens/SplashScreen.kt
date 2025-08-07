package com.example.doctor.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.doctor.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Animación de escala para el logo
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = EaseInOut)
        )
        delay(1500) // Tiempo que se queda el splash
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true } // Para que no regrese a splash con back
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Asegúrate que el logo está en drawable
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
        )
    }
}
