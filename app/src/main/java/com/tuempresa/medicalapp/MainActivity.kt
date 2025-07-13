package com.tuempresa.medicalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.tuempresa.medicalapp.ui.theme.Screens.LoginScreen
import com.tuempresa.medicalapp.ui.theme.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Comentar esta línea
        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            
            if (!isLoggedIn) {
                LoginScreen(onLoginSuccess = { isLoggedIn = true })
            } else {
                MainScreen()
            }
        }
    }
}
