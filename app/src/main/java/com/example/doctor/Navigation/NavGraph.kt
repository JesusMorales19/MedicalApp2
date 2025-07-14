package com.example.doctor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doctor.screens.*

enum class Screen {
    Home,
    Expediente,
    Cirugia,
    SignosVitales,
    Pacientes,
    DetalleCirugia  // Nueva pantalla
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.name) {
        composable(Screen.Home.name) {
            HomeScreen(navController)
        }
        composable(Screen.Expediente.name) {
            ExpedienteScreen(navController)
        }

        composable(Screen.Cirugia.name) {
            CirugiaScreen(navController)
        }

        composable(Screen.Pacientes.name) {
            PacientesScreen(navController)
        }
        composable(Screen.DetalleCirugia.name) {  // Navegación a DetalleCirugiaScreen
            DetalleCirugiaScreen(navController)
        }
    }
}
