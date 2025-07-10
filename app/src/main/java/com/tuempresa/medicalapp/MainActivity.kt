package com.tuempresa.medicalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.tuempresa.medicalapp.ui.theme.Screens.LoginScreen
import com.tuempresa.medicalapp.ui.theme.Screens.PacientesScreen
import com.tuempresa.medicalapp.ui.theme.Screens.DetallePacienteScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf("login") }
            var selectedPaciente by remember { mutableStateOf("Ricardo Israel") }
            
            when (currentScreen) {
                "login" -> {
                    LoginScreen(onLoginSuccess = { currentScreen = "pacientes" })
                }
                "pacientes" -> {
                    PacientesScreen(
                        onPacienteClick = { paciente ->
                            selectedPaciente = paciente
                            currentScreen = "detalle"
                        }
                    )
                }
                "detalle" -> {
                    DetallePacienteScreen(
                        nombre = selectedPaciente,
                        onBack = { currentScreen = "pacientes" },
                        onServiciosClick = { /* Navegar a servicios */ },
                        onHistorialClick = { /* Navegar a historial */ }
                    )
                }
            }
        }
    }
}
