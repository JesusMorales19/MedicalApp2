package com.tuempresa.medicalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.*
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuempresa.medicalapp.presentation.screens.auth.LoginScreen
import com.tuempresa.medicalapp.presentation.screens.doctor.MainScreen
import com.tuempresa.medicalapp.presentation.screens.secretary.SecretariaScreen
import com.tuempresa.medicalapp.presentation.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Comentar esta línea
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()
            
            // Variable para manejar el botón de regreso
            var onBackPressed by remember { mutableStateOf<(() -> Boolean)?>(null) }
            
            // Configurar el callback del botón de regreso
            DisposableEffect(Unit) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // Si hay una función de regreso definida, la ejecutamos
                        if (onBackPressed?.invoke() == true) {
                            // La función de regreso se ejecutó correctamente
                            return
                        }
                        // Si no hay función de regreso o retorna false, cerramos la app
                        finish()
                    }
                }
                onBackPressedDispatcher.addCallback(callback)
                onDispose {
                    callback.remove()
                }
            }
            
            if (!uiState.isAuthenticated) {
                LoginScreen(onLoginSuccess = { /* No se necesita lógica extra */ })
            } else {
                // Verificar el rol del usuario autenticado
                when (uiState.doctor?.rol) {
                    "secretaria" -> {
                        SecretariaScreen(
                            authViewModel = authViewModel,
                            onBackPressed = { onBackPressed = it }
                        )
                    }
                    "doctor" -> {
                        MainScreen(
                            authViewModel = authViewModel,
                            onBackPressed = { onBackPressed = it }
                        )
                    }
                    else -> {
                        // Por defecto, si no hay rol definido, asumimos que es doctor
                        MainScreen(
                            authViewModel = authViewModel,
                            onBackPressed = { onBackPressed = it }
                        )
                    }
                }
            }
        }
    }
}
