package com.tuempresa.medicalapp.presentation.screens.doctor

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tuempresa.medicalapp.core.network.ProyeccionService
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.models.Consulta
import java.net.NetworkInterface
import java.net.Inet4Address
import android.util.Log
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.os.Handler
import android.os.Looper

@Composable
fun ProyeccionScreen(
    pacientes: List<Paciente>,
    consultas: List<Consulta>,
    nombreDoctor: String = "",
    apellidoDoctor: String = "",
    fotoDoctor: String = ""
) {
    val context = LocalContext.current
    var serviceStarted by remember { mutableStateOf(false) }
    var serverStatus by remember { mutableStateOf("Desconectado") }
    var currentPort by remember { mutableStateOf(9095) }
    val localIp = remember { getLocalIpAddress() }

    // Verificar el estado inicial del servicio cuando se carga la pantalla
    LaunchedEffect(Unit) {
        val isRunning = ProyeccionService.isServiceRunning(context)
        serviceStarted = isRunning
        if (isRunning) {
            serverStatus = "Conectado"
            // Solicitar el estado actual del servicio para obtener el puerto correcto
            val intent = Intent(context, ProyeccionService::class.java)
            intent.action = ProyeccionService.ACTION_GET_STATUS
            context.startService(intent)
        }
        Log.i("ProyeccionScreen", "Estado inicial del servicio: $isRunning")
    }

    // Broadcast receiver para recibir actualizaciones del puerto
    val portUpdateReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ProyeccionService.ACTION_PORT_UPDATE) {
                    val port = intent.getIntExtra(ProyeccionService.EXTRA_PORT, -1)
                    val status = intent.getStringExtra(ProyeccionService.EXTRA_STATUS) ?: "Desconectado"
                    
                                         currentPort = if (port > 0) port else 9095
                    serverStatus = status
                    serviceStarted = port > 0 || status == "Conectado"
                    
                    Log.i("ProyeccionScreen", "Puerto actualizado: $port, Estado: $status, ServiceStarted: $serviceStarted")
                }
            }
        }
    }

    // Registrar el broadcast receiver
    DisposableEffect(context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            portUpdateReceiver,
            IntentFilter(ProyeccionService.ACTION_PORT_UPDATE)
        )
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(portUpdateReceiver)
        }
    }

    // Log de depuración cuando los datos están listos
    LaunchedEffect(pacientes, consultas) {
        if (pacientes.isNotEmpty() && consultas.isNotEmpty()) {
            Log.i("ProyeccionScreen", "Pacientes cargados: ${pacientes.size}")
            pacientes.forEach { Log.i("ProyeccionScreen", "Paciente: ${it.id} - ${it.nombre} ${it.apellido}") }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Proyección a Smart TV", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        if (pacientes.isEmpty() || consultas.isEmpty()) {
            Text("Cargando datos o no hay pacientes/consultas disponibles.", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("IP local del servidor:", style = MaterialTheme.typography.bodyLarge)
                Text(localIp ?: "No disponible", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Puerto: $currentPort", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("ws://${localIp ?: "<ip>"}:$currentPort", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Estado del servidor: $serverStatus", color = if (serviceStarted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            Button(
                onClick = {
                    Log.i("ProyeccionScreen", "🔘 Botón 'Iniciar servidor' presionado")
                    Log.i("ProyeccionScreen", "   - serviceStarted: $serviceStarted")
                    
                    if (!serviceStarted) {
                        Log.i("ProyeccionScreen", "🚀 Iniciando servicio...")
                        val intent = Intent(context, ProyeccionService::class.java)
                        Log.i("ProyeccionScreen", "   - Intent creado: ${intent.action}")
                        
                        try {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                Log.i("ProyeccionScreen", "   - Usando startForegroundService")
                                context.startForegroundService(intent)
                            } else {
                                Log.i("ProyeccionScreen", "   - Usando startService")
                                context.startService(intent)
                            }
                            Log.i("ProyeccionScreen", "✅ Servicio iniciado correctamente")
                            
                            // Enviar datos del doctor después de iniciar el servicio
                            if (nombreDoctor.isNotEmpty() && apellidoDoctor.isNotEmpty()) {
                                Log.i("ProyeccionScreen", "📤 Enviando datos del doctor después de iniciar servicio...")
                                
                                // Esperar un poco para que el servidor esté listo
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intentDatos = Intent(context, ProyeccionService::class.java)
                                    intentDatos.putExtra("nombreDoctor", nombreDoctor)
                                    intentDatos.putExtra("apellidoDoctor", apellidoDoctor)
                                    intentDatos.putExtra("fotoDoctor", fotoDoctor)
                                    context.startService(intentDatos)
                                    Log.i("ProyeccionScreen", "📤 Intent de datos enviado después del delay")
                                }, 2000) // 2 segundos de delay
                            }
                        } catch (e: Exception) {
                            Log.e("ProyeccionScreen", "❌ Error al iniciar servicio: ${e.message}")
                            Log.e("ProyeccionScreen", "❌ Stack trace: ${e.stackTraceToString()}")
                        }
                        // El estado se actualizará automáticamente via broadcast
                    } else {
                        Log.i("ProyeccionScreen", "ℹ️ Servicio ya está iniciado")
                    }
                },
                enabled = !serviceStarted
            ) {
                Text("Iniciar servidor")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val intent = Intent(context, ProyeccionService::class.java)
                    context.stopService(intent)
                    serverStatus = "Desconectado"
                    serviceStarted = false
                                         currentPort = 9095
                },
                enabled = serviceStarted
            ) {
                Text("Detener servidor")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (serviceStarted && nombreDoctor.isNotEmpty() && apellidoDoctor.isNotEmpty()) {
                    // Solo enviar datos del doctor
                    Log.i("ProyeccionScreen", "🔘 Botón presionado - Enviando datos del doctor")
                    Log.i("ProyeccionScreen", "   - Nombre: $nombreDoctor")
                    Log.i("ProyeccionScreen", "   - Apellido: $apellidoDoctor")
                    Log.i("ProyeccionScreen", "   - Foto: ${fotoDoctor.take(50)}...")
                    Log.i("ProyeccionScreen", "   - Servicio iniciado: $serviceStarted")
                    
                    val intent = Intent(context, ProyeccionService::class.java)
                    intent.putExtra("nombreDoctor", nombreDoctor)
                    intent.putExtra("apellidoDoctor", apellidoDoctor)
                    intent.putExtra("fotoDoctor", fotoDoctor)
                    context.startService(intent)
                    Toast.makeText(context, "Enviando datos del doctor a la TV...", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("ProyeccionScreen", "⚠️ No se pueden enviar datos:")
                    Log.w("ProyeccionScreen", "   - Servicio iniciado: $serviceStarted")
                    Log.w("ProyeccionScreen", "   - Nombre vacío: ${nombreDoctor.isEmpty()}")
                    Log.w("ProyeccionScreen", "   - Apellido vacío: ${apellidoDoctor.isEmpty()}")
                }
            },
            enabled = serviceStarted && nombreDoctor.isNotEmpty() && apellidoDoctor.isNotEmpty()
        ) {
            Text("Enviar datos del doctor a la TV")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Información adicional para el usuario
        if (serviceStarted) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("📱 Instrucciones para conectar la TV:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Asegúrate de que la TV esté en la misma red WiFi", style = MaterialTheme.typography.bodyMedium)
                    Text("2. En la TV, conecta a: ws://$localIp:$currentPort", style = MaterialTheme.typography.bodyMedium)
                    Text("3. Presiona 'Enviar datos del doctor' para probar", style = MaterialTheme.typography.bodyMedium)
                    Text("4. La TV recibirá los datos en tiempo real", style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Botón de prueba con datos hardcodeados
                    Button(
                        onClick = {
                            Log.i("ProyeccionScreen", "🧪 Botón de prueba presionado")
                            val intent = Intent(context, ProyeccionService::class.java)
                            intent.putExtra("nombreDoctor", "Dr. Prueba")
                            intent.putExtra("apellidoDoctor", "Test")
                            intent.putExtra("fotoDoctor", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==")
                            context.startService(intent)
                            Toast.makeText(context, "Enviando datos de prueba...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("🧪 Enviar datos de prueba")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botón de prueba simple
                    Button(
                        onClick = {
                            Log.i("ProyeccionScreen", "🧪 Botón de mensaje simple presionado")
                            // Llamar directamente al método de prueba del WebSocket
                            val intent = Intent(context, ProyeccionService::class.java)
                            intent.putExtra("mensajePrueba", true)
                            context.startService(intent)
                            Toast.makeText(context, "Enviando mensaje simple...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("📝 Enviar mensaje simple")
                    }
                }
            }
        }
    }
}

fun getLocalIpAddress(): String? {
    try {
        val en = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val intf = en.nextElement()
            val enumIpAddr = intf.inetAddresses
            while (enumIpAddr.hasMoreElements()) {
                val inetAddress = enumIpAddr.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    return inetAddress.hostAddress
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return null
} 