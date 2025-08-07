package com.tuempresa.medicalapp.core.network

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.pm.ServiceInfo
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tuempresa.medicalapp.R
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.models.Consulta
import com.tuempresa.medicalapp.data.models.PaqueteEnvio
import com.tuempresa.medicalapp.data.models.Cirugia
import com.google.gson.Gson
import java.io.File
import com.tuempresa.medicalapp.data.models.Radiografia
import com.tuempresa.medicalapp.data.models.Doctor
import java.net.ServerSocket
import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ProyeccionService : Service() {
    private var webSocketServer: DoctorWebSocketServer? = null
    private val CHANNEL_ID = "ProyeccionServiceChannel"
    private val NOTIF_ID = 1
    private var currentPort: Int = 9095

    companion object {
        const val ACTION_PORT_UPDATE = "com.tuempresa.medicalapp.PORT_UPDATE"
        const val EXTRA_PORT = "port"
        const val EXTRA_STATUS = "status"
        const val ACTION_GET_STATUS = "com.tuempresa.medicalapp.GET_STATUS"
        
        // Método para verificar si el servicio está ejecutándose
        fun isServiceRunning(context: Context?): Boolean {
            val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
            if (manager == null) return false
            
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (ProyeccionService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }

    // Función para encontrar un puerto disponible
    private fun findAvailablePort(startPort: Int = 9095, maxAttempts: Int = 10): Int? {
        for (port in startPort until startPort + maxAttempts) {
            try {
                ServerSocket(port).use { 
                    return port 
                }
            } catch (e: Exception) {
                // Puerto ocupado, continuar con el siguiente
                continue
            }
        }
        return null
    }

    // Función para enviar broadcast con el puerto actual
    private fun broadcastPortUpdate(port: Int, status: String) {
        val intent = Intent(ACTION_PORT_UPDATE).apply {
            putExtra(EXTRA_PORT, port)
            putExtra(EXTRA_STATUS, status)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        android.util.Log.i("ProyeccionService", "🚀 Iniciando servicio de proyección...")
        android.util.Log.i("ProyeccionService", "📋 Intent extras: ${intent?.extras?.keySet()?.joinToString()}")
        android.util.Log.i("ProyeccionService", "📋 Intent action: ${intent?.action}")
        android.util.Log.i("ProyeccionService", "📋 Intent data: ${intent?.data}")
        
        // Si el servidor ya está ejecutándose, enviar el estado actual
        if (webSocketServer != null && intent?.action == ACTION_GET_STATUS) {
            android.util.Log.i("ProyeccionService", "📤 Enviando estado actual del servidor")
            broadcastPortUpdate(currentPort, "Conectado")
            return START_STICKY
        }
        
        if (webSocketServer == null) {
            // Buscar un puerto disponible
            val availablePort = findAvailablePort(9095)
            if (availablePort == null) {
                android.util.Log.e("ProyeccionService", "❌ No se pudo encontrar un puerto disponible")
                broadcastPortUpdate(-1, "Error: No hay puertos disponibles")
                stopSelf()
                return START_NOT_STICKY
            }
            
            currentPort = availablePort
            android.util.Log.i("ProyeccionService", "🔧 Creando servidor WebSocket en puerto $availablePort...")
            webSocketServer = DoctorWebSocketServer(availablePort, this)
            try {
                webSocketServer?.start()
                android.util.Log.i("ProyeccionService", "✅ Servidor WebSocket iniciado exitosamente en puerto $availablePort")
                broadcastPortUpdate(availablePort, "Conectado")
            } catch (e: Exception) {
                android.util.Log.e("ProyeccionService", "❌ Error al iniciar servidor: ${e.message}")
                android.util.Log.e("ProyeccionService", "❌ Stack trace: ${e.stackTraceToString()}")
                
                // Si falla, intentar con otro puerto
                if (e.message?.contains("Address already in use") == true) {
                    android.util.Log.i("ProyeccionService", "🔄 Intentando con puerto alternativo...")
                    val alternativePort = findAvailablePort(availablePort + 1)
                    if (alternativePort != null) {
                        currentPort = alternativePort
                        webSocketServer = DoctorWebSocketServer(alternativePort, this)
                        try {
                            webSocketServer?.start()
                            android.util.Log.i("ProyeccionService", "✅ Servidor WebSocket iniciado exitosamente en puerto $alternativePort")
                            broadcastPortUpdate(alternativePort, "Conectado")
                        } catch (e2: Exception) {
                            android.util.Log.e("ProyeccionService", "❌ Error definitivo al iniciar servidor: ${e2.message}")
                            broadcastPortUpdate(-1, "Error: ${e2.message}")
                        }
                    } else {
                        broadcastPortUpdate(-1, "Error: No hay puertos disponibles")
                    }
                } else {
                    broadcastPortUpdate(-1, "Error: ${e.message}")
                }
            }
        } else {
            android.util.Log.i("ProyeccionService", "ℹ️ Servidor WebSocket ya está ejecutándose en puerto $currentPort")
            broadcastPortUpdate(currentPort, "Conectado")
        }
        
        // Verificar el tipo de envío
        val tipoEnvio = intent?.getStringExtra("tipoEnvio")
        val mensajePrueba = intent?.getBooleanExtra("mensajePrueba", false)
        
        // Manejar mensaje de prueba simple
        if (mensajePrueba == true) {
            android.util.Log.i("ProyeccionService", "🧪 Enviando mensaje de prueba simple...")
            webSocketServer?.enviarMensajePrueba()
            return START_STICKY
        }
        
        when (tipoEnvio) {

            "radiografias" -> {
                // Enviar radiografías específicas por tipo
                val tipoRadiografia = intent.getStringExtra("tipoRadiografia") ?: ""
                val radiografiasFilePath = intent.getStringExtra("radiografiasFilePath")
                
                if (radiografiasFilePath != null) {
                    val json = File(radiografiasFilePath).readText()
                    val radiografias = Gson().fromJson(json, Array<Radiografia>::class.java).toList()
                    
                    // Crear objeto Paciente con solo los datos necesarios
                    val paciente = Paciente(
                        nombre = intent.getStringExtra("pacienteNombre") ?: "",
                        apellido = intent.getStringExtra("pacienteApellido") ?: ""
                    )
                    
                    webSocketServer?.enviarRadiografiasPorTipo(paciente, radiografias, tipoRadiografia)
                }
            }
            "consultaEspecifica" -> {
                // Enviar consulta específica
                val consultaFilePath = intent.getStringExtra("consultaFilePath")
                val pacienteNombre = intent.getStringExtra("pacienteNombre") ?: ""
                val pacienteApellido = intent.getStringExtra("pacienteApellido") ?: ""
                val pacienteId = intent.getStringExtra("pacienteId") ?: ""
                val pacienteSexo = intent.getStringExtra("pacienteSexo") ?: ""
                val pacienteTipoSangre = intent.getStringExtra("pacienteTipoSangre") ?: ""
                val pacienteFoto = intent.getStringExtra("pacienteFoto") ?: ""
                
                // Datos del médico
                val nombreDoctor = intent.getStringExtra("nombreDoctor")
                val apellidoDoctor = intent.getStringExtra("apellidoDoctor")
                val especialidadDoctor = intent.getStringExtra("especialidadDoctor")
                val fotoDoctor = intent.getStringExtra("fotoDoctor")
                
                if (consultaFilePath != null) {
                    val json = File(consultaFilePath).readText()
                    val consulta = Gson().fromJson(json, Consulta::class.java)
                    
                    // Crear objeto Paciente con todos los datos necesarios
                    val paciente = Paciente(
                        id = pacienteId,
                        nombre = pacienteNombre,
                        apellido = pacienteApellido,
                        sexo = pacienteSexo,
                        tipoSangre = pacienteTipoSangre,
                        fotoBase64 = pacienteFoto
                    )
                    
                    // Crear objeto Doctor si están disponibles los datos
                    val doctor = if (nombreDoctor != null && apellidoDoctor != null) {
                        Doctor(
                            nombre = nombreDoctor,
                            apellido = apellidoDoctor,
                            especialidad = especialidadDoctor ?: "",
                            fotoBase64 = fotoDoctor ?: ""
                        )
                    } else null
                    
                    android.util.Log.i("ProyeccionService", "📤 Enviando consulta específica...")
                    android.util.Log.i("ProyeccionService", "   - Paciente: $pacienteNombre $pacienteApellido")
                    android.util.Log.i("ProyeccionService", "   - Sexo: $pacienteSexo")
                    android.util.Log.i("ProyeccionService", "   - Tipo sangre: $pacienteTipoSangre")
                    android.util.Log.i("ProyeccionService", "   - Tamaño foto paciente: ${pacienteFoto.length} caracteres")
                    
                    webSocketServer?.enviarConsultaEspecifica(paciente, consulta, doctor)
                }
            }
            "cirugiaEspecifica" -> {
                // Enviar cirugía específica
                val cirugiaFilePath = intent.getStringExtra("cirugiaFilePath")
                val pacienteNombre = intent.getStringExtra("pacienteNombre") ?: ""
                val pacienteApellido = intent.getStringExtra("pacienteApellido") ?: ""
                val pacienteId = intent.getStringExtra("pacienteId") ?: ""
                val pacienteFoto = intent.getStringExtra("pacienteFoto") ?: ""
                
                // Solo datos básicos del médico
                val nombreDoctor = intent.getStringExtra("nombreDoctor")
                val apellidoDoctor = intent.getStringExtra("apellidoDoctor")
                
                if (cirugiaFilePath != null) {
                    android.util.Log.i("ProyeccionService", "📤 Enviando cirugía específica...")
                    android.util.Log.i("ProyeccionService", "   - Paciente: $pacienteNombre $pacienteApellido")
                    android.util.Log.i("ProyeccionService", "   - Doctor: $nombreDoctor $apellidoDoctor")
                    android.util.Log.i("ProyeccionService", "   - Tamaño foto paciente: ${pacienteFoto.length} caracteres")
                    
                    val json = File(cirugiaFilePath).readText()
                    val cirugia = Gson().fromJson(json, Cirugia::class.java)
                    
                    // Crear objeto Paciente con solo los datos necesarios
                    val paciente = Paciente(
                        id = pacienteId,
                        nombre = pacienteNombre,
                        apellido = pacienteApellido,
                        fotoBase64 = pacienteFoto
                    )
                    
                    // Crear objeto Doctor solo con nombre y apellido
                    val doctor = if (nombreDoctor != null && apellidoDoctor != null) {
                        Doctor(
                            nombre = nombreDoctor,
                            apellido = apellidoDoctor
                        )
                    } else null
                    
                    webSocketServer?.enviarCirugiaEspecifica(paciente, cirugia, doctor)
                }
            }
            else -> {
                // Envío de consultas (código existente)
                val filePath = intent?.getStringExtra("paqueteFilePath")
                if (filePath != null) {
                    val json = File(filePath).readText()
                    val paquete = Gson().fromJson(json, PaqueteEnvio::class.java)
                    webSocketServer?.enviarUltimasConsultas(listOf(paquete.paciente), paquete.consultas)
                }
                
                // Enviar datos del doctor si se proporcionan
                val nombreDoctor = intent?.getStringExtra("nombreDoctor")
                val apellidoDoctor = intent?.getStringExtra("apellidoDoctor")
                val fotoDoctor = intent?.getStringExtra("fotoDoctor")
                
                android.util.Log.i("ProyeccionService", "🔍 Datos del doctor recibidos:")
                android.util.Log.i("ProyeccionService", "   - Nombre: $nombreDoctor")
                android.util.Log.i("ProyeccionService", "   - Apellido: $apellidoDoctor")
                android.util.Log.i("ProyeccionService", "   - Foto: ${fotoDoctor?.take(50)}...")
                android.util.Log.i("ProyeccionService", "   - WebSocket server: ${webSocketServer != null}")
                
                if (nombreDoctor != null && apellidoDoctor != null && fotoDoctor != null) {
                    android.util.Log.i("ProyeccionService", "📤 Enviando datos del doctor a la TV...")
                    webSocketServer?.enviarDatosDoctor(nombreDoctor, apellidoDoctor, fotoDoctor)
                    android.util.Log.i("ProyeccionService", "✅ Datos del doctor enviados")
                } else {
                    android.util.Log.w("ProyeccionService", "⚠️ Datos del doctor incompletos, no se envían")
                }
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIF_ID, createNotification())
        }
        return START_STICKY
    }

    override fun onDestroy() {
        android.util.Log.i("ProyeccionService", "🛑 Servicio siendo destruido...")
        webSocketServer?.stop()
        // Enviar broadcast de que el servicio se detuvo
        broadcastPortUpdate(-1, "Desconectado")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        createNotificationChannel()
        val notificationIntent = packageManager?.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Proyección activa")
            .setContentText("El servidor de proyección está en ejecución")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Proyección Smart TV",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
