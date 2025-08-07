package com.tuempresa.medicalapp.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuempresa.medicalapp.R
import com.tuempresa.medicalapp.presentation.viewmodels.AuthViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import android.util.Log

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var shouldNavigate by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val uiState by authViewModel.uiState.collectAsState()
    
    // Observar cambios en la autenticación y datos del usuario
    LaunchedEffect(uiState.isAuthenticated, uiState.doctor) {
        Log.d("LOGIN_DEBUG", "isAuthenticated: ${uiState.isAuthenticated}, doctor: ${uiState.doctor?.nombre}")
        if (uiState.isAuthenticated && uiState.doctor != null) {
            Log.d("LOGIN_DEBUG", "Mostrando alerta de éxito para: ${uiState.doctor?.nombre}")
            showSuccessAlert = true
        } else if (!uiState.isAuthenticated) {
            // Resetear estados cuando no está autenticado
            showSuccessAlert = false
            shouldNavigate = false
        }
    }
    
    // Navegar después de que se cierre la alerta
    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            onLoginSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .background(Color(0xFFF5F8FC), shape = RoundedCornerShape(16.dp))
                .border(2.dp, Color.Black.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp))
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_medical_logo),
                contentDescription = "Medical Logo",
                modifier = Modifier
                    .size(220.dp)
                    .padding(bottom = 16.dp)
            )

            // Título
            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color(0xFF183A6D),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Mostrar error si existe
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Botón
            Button(
                onClick = { 
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        authViewModel.iniciarSesion(email, password)
                    }
                },
                enabled = !uiState.isLoading && email.isNotEmpty() && password.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183A6D))
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Log in", color = Color.White)
                }
            }
        }
    }
    
    // Alerta de éxito
    if (showSuccessAlert && uiState.doctor != null) {
        Log.d("LOGIN_DEBUG", "Renderizando alerta de éxito")
        val doctor = uiState.doctor!!
        val nombreCompleto = "${doctor.nombre} ${doctor.apellido}".trim()
        val esSecretaria = doctor.rol.lowercase() == "secretaria"
        val titulo = if (esSecretaria) "¡Inicio de sesión exitoso!" else "¡Inicio de sesión exitoso!"
        val mensaje = if (esSecretaria) {
            "Bienvenida(o) Secretaria(o) $nombreCompleto"
        } else {
            "Bienvenido(a) Doctor(a) $nombreCompleto"
        }
        
        AlertDialog(
            onDismissRequest = { },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(titulo, fontWeight = FontWeight.Bold, color = Color.Green)
                }
            },
            text = { Text(mensaje) },
            confirmButton = {
                Button(
                    onClick = { 
                        showSuccessAlert = false
                        shouldNavigate = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("OK", color = Color.LightGray)
                }
            }
        )
    }
}