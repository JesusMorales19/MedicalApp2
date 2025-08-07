package com.example.doctor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctor.navigation.Screen
import com.example.doctor.Model.TVDataViewModel
import com.example.doctor.core.network.TVWebSocketClient
import java.net.URI
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun ConfigScreen(navController: NavController, tvDataViewModel: TVDataViewModel) {
    var ip by remember { mutableStateOf("") }
    var connecting by remember { mutableStateOf(false) }
    var connected by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var wsClient by remember { mutableStateOf<TVWebSocketClient?>(null) }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1565C0),
                        Color(0xFF1976D2),
                        Color(0xFF42A5F5)
                    )
                )
            )
    ) {
        // Fondo decorativo con círculos
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 300.dp, y = 100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con icono y título
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF1565C0)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Configuración de Conexión",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Conecta con el sistema del doctor",
                        fontSize = 16.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Card principal con el formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Campo de IP con icono
                    OutlinedTextField(
                        value = ip,
                        onValueChange = { ip = it },
                        label = { Text("Dirección IP del doctor") },
                        placeholder = { Text("Ejemplo: 192.168.1.100") },
                        singleLine = true,
                        enabled = !connecting && !connected,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = null,
                                tint = Color(0xFF1565C0)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            unfocusedBorderColor = Color(0xFFCCCCCC),
                            focusedLabelColor = Color(0xFF1565C0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Botón de prueba de conectividad
                    Button(
                        onClick = {
                            if (ip.isNotEmpty()) {
                                // Probar conectividad básica
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    try {
                                        val socket = java.net.Socket()
                                        socket.connect(java.net.InetSocketAddress(ip, 9095), 5000)
                                        socket.close()
                                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                                                         error = "✅ Puerto 9095 está abierto en $ip"
                                        }
                                    } catch (e: Exception) {
                                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                                                         error = "❌ No se puede conectar al puerto 9095: ${e.message}"
                                        }
                                    }
                                }
                            }
                        },
                        enabled = ip.isNotEmpty() && !connecting,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF666666))
                    ) {
                        Text("Probar conectividad")
                    }

                    // Botón de conexión
                    Button(
                        onClick = {
                            if (ip.isNotEmpty()) {
                                connecting = true
                                error = null
                                try {
                                                                         Log.d("ConfigScreen", "Intentando conectar a ws://$ip:9095")
                                    val ws = TVWebSocketClient(
                                                                                 URI("ws://$ip:9095"),
                                        onDataReceived = { message ->
                                            // Procesar el mensaje usando el nuevo método del ViewModel
                                            tvDataViewModel.procesarMensajeWebSocket(message)
                                        },
                                        onConnectionEstablished = {
                                            // Actualizar estado cuando se establece la conexión
                                            connected = true
                                            connecting = false
                                            Log.d("ConfigScreen", "Conexión establecida exitosamente")
                                            Log.d("ConfigScreen", "Aplicación lista para recibir datos del servidor")
                                            Log.d("ConfigScreen", "Esperando mensajes WebSocket...")
                                        }
                                    )
                                    
                                    // Configurar timeout de conexión
                                    ws.setConnectionLostTimeout(10)
                                    wsClient = ws
                                    
                                    // Configurar timeout de conexión
                                    ws.setConnectionLostTimeout(10)
                                    ws.connect()
                                    
                                    // Timeout manual después de 30 minutos
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        kotlinx.coroutines.delay(1800000) // 30 minutos
                                        if (connecting) {
                                            error = "Timeout: No se pudo conectar después de 30 minutos"
                                            connecting = false
                                            ws.close()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("ConfigScreen", "Error al crear WebSocket", e)
                                    error = "Error de conexión: ${e.message}"
                                    connecting = false
                                }
                            } else {
                                error = "Por favor ingresa una IP válida"
                            }
                        },
                        enabled = !connecting && !connected && ip.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (connected) Color(0xFF4CAF50) else Color(0xFF1565C0)
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (connecting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Conectando...")
                            } else if (connected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Conectado")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Wifi,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Conectar")
                            }
                        }
                    }

                    // Mensajes de estado
                    if (error != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = error!!,
                                    color = Color(0xFFD32F2F),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    
                    if (connected) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "¡Conexión exitosa!",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 