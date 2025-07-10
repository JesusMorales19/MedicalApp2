package com.tuempresa.medicalapp.ui.theme.Screens

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
import com.tuempresa.medicalapp.R

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                .border(2.dp, Color.Black.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)) // Borde con opacidad
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
                    .padding(bottom = 24.dp)
            )

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Botón
            Button(
                onClick = { onLoginSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183A6D))
            ) {
                Text("Log in", color = Color.White)
            }
        }
    }
}