package com.example.doctor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doctor.screens.*

enum class Screen {
    Splash,
    Home,
    Expediente,
    Cirugia,
    SignosVitales,
    Pacientes,
    DetalleCirugia,
    Pendientes,
    Informacion,
    signosVitales



}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.name) {
        composable(Screen.Splash.name) {
            SplashScreen(navController)
        }
        composable(Screen.Home.name) {
            HomeScreen(navController)
        }
        composable(Screen.Expediente.name) {
            ExpedienteScreen(navController)
        }
        composable(Screen.Pacientes.name) {
            PacientesScreen(navController)
        }
        composable(Screen.DetalleCirugia.name) {
            DetalleCirugiaScreen(navController)
        }
        composable(Screen.Pendientes.name) {
            PendientesScreen(navController)
        }
        composable("informacion") {
            InformacionScreen(navController)
        }
        composable("signosVitales") {
            SignosVitalesScreen()
        }
    }
}
