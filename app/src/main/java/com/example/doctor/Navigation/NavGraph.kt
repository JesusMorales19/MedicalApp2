package com.example.doctor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doctor.screens.*
import com.example.doctor.Model.TVDataViewModel
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.tv.material3.Text
import kotlin.math.log


enum class Screen {
    Splash,
    Config,
    Home,
    Expediente,
    Cirugia,
    SignosVitales,
    Pacientes,
    DetalleCirugia,
    CirugiaEspecifica,
    ConsultaEspecifica,
    Pendientes,
    Informacion,
    signosVitales
}

@Composable
fun NavGraph(navController: NavHostController, tvDataViewModel: TVDataViewModel) {
    // Navegación automática global según el tipo de mensaje recibido
    val mensajeRecibido = tvDataViewModel.mensajeRecibido
    LaunchedEffect(mensajeRecibido) {
        Log.d("NavGraph", "LaunchedEffect ejecutado - mensajeRecibido: $mensajeRecibido")
        when (mensajeRecibido) {
            "datosDoctor" -> {
                Log.d("NavGraph", "Navegando a HomeScreen")
                navController.navigate(Screen.Home.name) {
                    popUpTo(Screen.Config.name) { inclusive = true }
                }
            }
            "datosPaciente" -> {
                Log.d("NavGraph", "Navegando a ExpedienteScreen")
                navController.navigate(Screen.Expediente.name) {
                    popUpTo(Screen.Config.name) { inclusive = true }
                }
            }
            "cirugiaEspecifica" -> {
                Log.d("NavGraph", "Navegando a CirugiaEspecificaScreen")
                navController.navigate(Screen.CirugiaEspecifica.name) {
                    popUpTo(Screen.Config.name) { inclusive = true }
                }
            }
            "consultaEspecifica" -> {
                Log.d("NavGraph", "Navegando a ConsultaEspecificaScreen")
                navController.navigate(Screen.ConsultaEspecifica.name) {
                    popUpTo(Screen.Config.name) { inclusive = true }
                }
            }
            else -> {
                Log.d("NavGraph", "Tipo de mensaje no reconocido: $mensajeRecibido")
            }
        }
    }
    
    NavHost(navController = navController, startDestination = Screen.Config.name) {
        composable(Screen.Splash.name) {
            SplashScreen(navController)
        }
        composable(Screen.Config.name) {
            ConfigScreen(navController, tvDataViewModel)
        }
        composable(Screen.Home.name) {
            HomeScreen(navController, tvDataViewModel)
        }
        composable(Screen.Expediente.name) {
            ExpedienteScreen(navController, tvDataViewModel)
        }
        composable(Screen.DetalleCirugia.name) {
            DetalleCirugiaScreen(navController)
        }
        composable(Screen.CirugiaEspecifica.name) {
            CirugiaEspecificaScreen(navController, tvDataViewModel.cirugiaEspecificaData)
        }
        composable(Screen.ConsultaEspecifica.name) {
            ConsultaEspecificaScreen(navController, tvDataViewModel.consultaEspecificaData)
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
