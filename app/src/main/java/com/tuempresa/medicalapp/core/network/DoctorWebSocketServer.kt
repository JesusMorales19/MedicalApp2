package com.tuempresa.medicalapp.core.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import org.java_websocket.server.WebSocketServer
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.net.InetSocketAddress
import org.json.JSONArray
import org.json.JSONObject
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.models.Consulta
import com.tuempresa.medicalapp.data.models.Radiografia
import android.content.Context
import com.tuempresa.medicalapp.data.models.Doctor
import com.tuempresa.medicalapp.data.models.Cirugia

class DoctorWebSocketServer(
    port: Int,
    private val context: Context? = null // Para mostrar Toasts
) : WebSocketServer(InetSocketAddress("0.0.0.0", port)) {

    private var tvConnection: WebSocket? = null

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        tvConnection = conn
        Log.i("WebSocketServer", "🎉 TV conectada: ${conn.remoteSocketAddress}")
        Log.i("WebSocketServer", "🔗 Handshake: ${handshake.resourceDescriptor}")
        Log.i("WebSocketServer", "🔗 Estado de conexión: ${conn.isOpen}")
        Log.i("WebSocketServer", "🔗 IP del cliente: ${conn.remoteSocketAddress?.address?.hostAddress}")
        Log.i("WebSocketServer", "🔗 Puerto del cliente: ${conn.remoteSocketAddress?.port}")
        context?.let {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(it, "TV conectada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        if (conn == tvConnection) tvConnection = null
        Log.i("WebSocketServer", "TV desconectada: ${conn.remoteSocketAddress}")
        context?.let {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(it, "TV desconectada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMessage(conn: WebSocket, message: String) {
        // Puedes manejar mensajes de la TV si lo necesitas
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        Log.e("WebSocketServer", "❌ Error en WebSocket: ${ex.message}")
        Log.e("WebSocketServer", "❌ Stack trace: ${ex.stackTraceToString()}")
        Log.e("WebSocketServer", "❌ Tipo de error: ${ex.javaClass.simpleName}")
        context?.let {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(it, "Error en WebSocket: ${ex.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        Log.i("WebSocketServer", "✅ Servidor WebSocket iniciado en puerto ${address.port}")
        Log.i("WebSocketServer", "📍 Dirección: ${address.hostString}:${address.port}")
        Log.i("WebSocketServer", "🔧 Estado del servidor: Ejecutándose")
        Log.i("WebSocketServer", "🔧 Conexiones activas: ${connections.size}")
        Log.i("WebSocketServer", "🔧 Bind address: 0.0.0.0 (acepta conexiones de cualquier IP)")
        Log.i("WebSocketServer", "🔧 Puerto: ${address.port}")
        Log.i("WebSocketServer", "🔧 Esperando conexiones entrantes...")
        context?.let {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(it, "Servidor WebSocket iniciado en puerto ${address.port}", Toast.LENGTH_LONG).show()
            }
        }
    }
    


    fun enviarUltimasConsultas(
        pacientes: List<Paciente>,
        consultas: List<Consulta>
    ) {
        val result = JSONArray()
        pacientes.forEach { paciente ->
            // Obtener TODAS las consultas del paciente ordenadas por fecha (más reciente primero)
            val consultasPaciente = consultas
                .filter { it.pacienteId == paciente.id }
                .sortedByDescending { it.fecha }
            
            val obj = JSONObject()
            obj.put("paciente", JSONObject().apply {
                put("id", paciente.id)
                put("foto", paciente.fotoBase64)
                put("nombre", paciente.nombre)
                put("apellido", paciente.apellido)
                put("sexo", paciente.sexo)
                put("telefono", paciente.telefono)
                put("CURP", paciente.curp)
                put("email", paciente.email)
                put("edad", paciente.edad)
                put("direccion", paciente.direccion)
                put("tipoSangre", paciente.tipoSangre)
                put("fechaNacimiento", paciente.fechaNacimiento)
            })

            // Verificar si el paciente tiene consultas
            if (consultasPaciente.isNotEmpty()) {
                // ÚLTIMA CONSULTA con todos los datos completos
                val ultimaConsulta = consultasPaciente.first()
                val ultimaConsultaJson = JSONObject().apply {
                    put("fecha", ultimaConsulta.fecha)
                    put("motivo", ultimaConsulta.motivo)
                    put("hora", ultimaConsulta.hora)
                    put("diagnosticoPrincipal", ultimaConsulta.diagnosticoPrincipal)
                    put("diagnosticoSecundario", ultimaConsulta.diagnosticoSecundario)
                    put("enfermedadesCronicas", ultimaConsulta.enfermedadesCronicas)
                    put("alergias", ultimaConsulta.alergias)
                    put("peso", ultimaConsulta.peso)
                    put("altura", ultimaConsulta.altura)
                    put("notas", ultimaConsulta.notas)
                    put("estudios", ultimaConsulta.estudios)
                    put("indicaciones", ultimaConsulta.indicaciones)
                    put("requiereSeguimiento", ultimaConsulta.requiereSeguimiento)
                    put("fechaProxima", ultimaConsulta.fechaProxima)
                    put("presionArterial", ultimaConsulta.presionArterial)
                    put("temperatura", ultimaConsulta.temperatura)
                    put("frecuenciaCardiaca", ultimaConsulta.frecuenciaCardiaca)

                    // Medicamentos de la última consulta
                    val medicamentosJsonArray = JSONArray()
                    ultimaConsulta.medicamentos.forEach { med ->
                        val medJson = JSONObject()
                        medJson.put("nombre", med.nombre)
                        medJson.put("dosis", med.dosis)
                        medJson.put("frecuencia", med.frecuencia)
                        medicamentosJsonArray.put(medJson)
                    }
                    put("medicamentos", medicamentosJsonArray)
                }

                // CONSULTAS ANTERIORES solo con fecha y motivo
                val consultasAnterioresJsonArray = JSONArray()
                if (consultasPaciente.size > 1) {
                    // Empezar desde el índice 1 (la segunda consulta) ya que la primera es la última
                    for (i in 1 until consultasPaciente.size) {
                        val consultaAnterior = consultasPaciente[i]
                        val consultaAnteriorJson = JSONObject().apply {
                            put("fecha", consultaAnterior.fecha)
                            put("motivo", consultaAnterior.motivo)
                        }
                        consultasAnterioresJsonArray.put(consultaAnteriorJson)
                    }
                }

                // Estructura final del JSON con consultas
                obj.put("ultimaConsulta", ultimaConsultaJson)
                obj.put("consultasAnteriores", consultasAnterioresJsonArray)
                
                Log.i("WebSocketServer", "📤 Enviando datos del paciente ${paciente.nombre} ${paciente.apellido} CON consultas")
            } else {
                // Si no hay consultas, enviar consultas vacías
                val ultimaConsultaVacia = JSONObject().apply {
                    put("fecha", "")
                    put("motivo", "")
                    put("hora", "")
                    put("diagnosticoPrincipal", "")
                    put("diagnosticoSecundario", "")
                    put("enfermedadesCronicas", "")
                    put("alergias", "")
                    put("peso", "")
                    put("altura", "")
                    put("notas", "")
                    put("estudios", "")
                    put("indicaciones", "")
                    put("requiereSeguimiento", false)
                    put("fechaProxima", "")
                    put("presionArterial", "")
                    put("temperatura", "")
                    put("frecuenciaCardiaca", "")
                    put("medicamentos", JSONArray()) // Array vacío de medicamentos
                }
                
                obj.put("ultimaConsulta", ultimaConsultaVacia)
                obj.put("consultasAnteriores", JSONArray()) // Array vacío de consultas anteriores
                
                Log.i("WebSocketServer", "📤 Enviando datos del paciente ${paciente.nombre} ${paciente.apellido} SIN consultas")
            }

            result.put(obj)
        }
        val jsonString = result.toString()
        Log.i("WebSocketServer", "JSON enviado: $jsonString")
        if (tvConnection != null) {
            tvConnection?.send(jsonString)
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "Datos enviados a la TV", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.w("WebSocketServer", "Intento de enviar datos sin cliente conectado")
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "No hay TV conectada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Método de prueba simple
    fun enviarMensajePrueba() {
        Log.i("WebSocketServer", "🧪 Enviando mensaje de prueba...")
        val mensajePrueba = "Hola TV, esto es una prueba desde Android!"
        
        if (tvConnection != null) {
            try {
                tvConnection?.send(mensajePrueba)
                Log.i("WebSocketServer", "✅ Mensaje de prueba enviado exitosamente")
                context?.let {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(it, "Mensaje de prueba enviado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocketServer", "❌ Error al enviar mensaje de prueba: ${e.message}")
            }
        } else {
            Log.w("WebSocketServer", "⚠️ No hay TV conectada para enviar mensaje de prueba")
        }
    }

    fun enviarDatosDoctor(
        nombreDoctor: String,
        apellidoDoctor: String,
        fotoDoctor: String
    ) {
        Log.i("WebSocketServer", "🚀 Iniciando envío de datos del doctor...")
        Log.i("WebSocketServer", "   - Nombre: $nombreDoctor")
        Log.i("WebSocketServer", "   - Apellido: $apellidoDoctor")
        Log.i("WebSocketServer", "   - Foto: ${fotoDoctor.take(50)}...")
        Log.i("WebSocketServer", "   - TV conectada: ${tvConnection != null}")
        Log.i("WebSocketServer", "   - Estado del servidor: Ejecutándose")
        Log.i("WebSocketServer", "   - Conexiones activas: ${connections.size}")
        Log.i("WebSocketServer", "   - Estado de tvConnection: ${tvConnection?.isOpen}")
        
        val obj = JSONObject().apply {
            put("tipo", "datosDoctor")
            put("doctor", JSONObject().apply {
                put("nombre", nombreDoctor)
                put("apellido", apellidoDoctor)
                put("foto", fotoDoctor)
            })
        }
        
        val jsonString = obj.toString()
        Log.i("WebSocketServer", "📤 JSON preparado: $jsonString")
        
        if (tvConnection != null) {
            try {
                tvConnection?.send(jsonString)
                Log.i("WebSocketServer", "✅ Datos del doctor enviados exitosamente")
                context?.let {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(it, "Datos del doctor enviados a la TV", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocketServer", "❌ Error al enviar datos del doctor: ${e.message}")
                Log.e("WebSocketServer", "❌ Stack trace: ${e.stackTraceToString()}")
            }
        } else {
            Log.w("WebSocketServer", "⚠️ Intento de enviar datos del doctor sin cliente conectado")
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "No hay TV conectada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun enviarRadiografiasPorTipo(
        paciente: Paciente,
        radiografias: List<Radiografia>,
        tipoRadiografia: String
    ) {
        // Filtrar radiografías por tipo
        val radiografiasFiltradas = radiografias.filter { it.tipo.equals(tipoRadiografia, ignoreCase = true) }
        
        if (radiografiasFiltradas.isEmpty()) {
            Log.w("WebSocketServer", "No hay radiografías de tipo $tipoRadiografia para el paciente ${paciente.nombre}")
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "No hay radiografías de $tipoRadiografia", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }

        val obj = JSONObject().apply {
            put("tipo", "radiografias")
            put("tipoRadiografia", tipoRadiografia)
            put("paciente", JSONObject().apply {
                put("nombre", paciente.nombre)
                put("apellido", paciente.apellido)
            })
            
            val radiografiasJsonArray = JSONArray()
            radiografiasFiltradas.forEach { radiografia ->
                val radiografiaJson = JSONObject().apply {
                    put("fecha", radiografia.fecha)
                    put("imagenBase64", radiografia.imagenBase64)
                    put("observaciones", radiografia.observaciones)
                }
                radiografiasJsonArray.put(radiografiaJson)
            }
            put("radiografias", radiografiasJsonArray)
        }
        
        val jsonString = obj.toString()
        Log.i("WebSocketServer", "Radiografías de $tipoRadiografia enviadas: $jsonString")
        
        if (tvConnection != null) {
            tvConnection?.send(jsonString)
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "Radiografías de $tipoRadiografia enviadas a la TV", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.w("WebSocketServer", "Intento de enviar radiografías sin cliente conectado")
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "No hay TV conectada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun enviarConsultaEspecifica(
        paciente: Paciente,
        consulta: Consulta,
        doctor: Doctor? = null
    ) {
        val obj = JSONObject().apply {
            put("tipo", "consultaEspecifica")
            put("paciente", JSONObject().apply {
                put("id", paciente.id)
                put("nombre", paciente.nombre)
                put("apellido", paciente.apellido)
                put("foto", paciente.fotoBase64)
                put("tipoSangre", paciente.tipoSangre)
                put("sexo", paciente.sexo)
            })
            
            // Datos del médico si están disponibles
            if (doctor != null) {
                put("doctor", JSONObject().apply {
                    put("id", doctor.id)
                    put("nombre", doctor.nombre)
                    put("apellido", doctor.apellido)
                    put("especialidad", doctor.especialidad)
                    put("foto", doctor.fotoBase64)
                })
            }
            
            put("consulta", JSONObject().apply {
                put("id", consulta.id)
                put("fecha", consulta.fecha)
                put("hora", consulta.hora)
                put("motivo", consulta.motivo)
                put("duracionSintomas", consulta.duracionSintomas)
                put("diagnosticoPrincipal", consulta.diagnosticoPrincipal)
                put("diagnosticoSecundario", consulta.diagnosticoSecundario)
                put("enfermedadesCronicas", consulta.enfermedadesCronicas)
                put("alergias", consulta.alergias)
                put("peso", consulta.peso)
                put("altura", consulta.altura)
                put("notas", consulta.notas)
                put("estudios", consulta.estudios)
                put("indicaciones", consulta.indicaciones)
                put("requiereSeguimiento", consulta.requiereSeguimiento)
                put("fechaProxima", consulta.fechaProxima)
                put("presionArterial", consulta.presionArterial)
                put("temperatura", consulta.temperatura)
                put("frecuenciaCardiaca", consulta.frecuenciaCardiaca)

                // Síntomas como lista separada
                val sintomasJsonArray = JSONArray()
                consulta.sintomas.forEach { sintoma ->
                    sintomasJsonArray.put(sintoma)
                }
                put("sintomas", sintomasJsonArray)

                // Medicamentos de la consulta
                val medicamentosJsonArray = JSONArray()
                consulta.medicamentos.forEach { med ->
                    val medJson = JSONObject()
                    medJson.put("nombre", med.nombre)
                    medJson.put("dosis", med.dosis)
                    medJson.put("frecuencia", med.frecuencia)
                    medicamentosJsonArray.put(medJson)
                }
                put("medicamentos", medicamentosJsonArray)
            })
        }
        
        val jsonString = obj.toString()
        Log.i("WebSocketServer", "📤 Consulta específica enviada")
        Log.i("WebSocketServer", "   - Paciente: ${paciente.nombre} ${paciente.apellido}")
        Log.i("WebSocketServer", "   - Sexo: ${paciente.sexo}")
        Log.i("WebSocketServer", "   - Tipo sangre: ${paciente.tipoSangre}")
        Log.i("WebSocketServer", "   - Tamaño foto: ${paciente.fotoBase64.length} caracteres")
        Log.i("WebSocketServer", "   - Doctor: ${doctor?.nombre} ${doctor?.apellido}")
        Log.i("WebSocketServer", "   - Tamaño JSON: ${jsonString.length} caracteres")
        
        if (tvConnection != null) {
            tvConnection?.send(jsonString)
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "Consulta específica enviada a la TV", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.w("WebSocketServer", "Intento de enviar consulta específica sin cliente conectado")
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "No hay TV conectada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun enviarCirugiaEspecifica(
        paciente: Paciente,
        cirugia: Cirugia,
        doctor: Doctor? = null
    ) {
        val obj = JSONObject().apply {
            put("tipo", "cirugiaEspecifica")
            put("paciente", JSONObject().apply {
                put("id", paciente.id)
                put("nombre", paciente.nombre)
                put("apellido", paciente.apellido)
                put("foto", paciente.fotoBase64)
            })
            
            // Solo datos básicos del médico (sin foto ni especialidad)
            if (doctor != null) {
                put("doctor", JSONObject().apply {
                    put("id", doctor.id)
                    put("nombre", doctor.nombre)
                    put("apellido", doctor.apellido)
                })
            }
            
            put("cirugia", JSONObject().apply {
                put("id", cirugia.id)
                put("nombreCirugia", cirugia.nombreCirugia)
                put("fecha", cirugia.fecha)
                put("nombreDoctor", cirugia.nombreDoctor)
                put("tipoProcesamiento", cirugia.tipoProcesamiento)
                put("tiempoRecuperacion", cirugia.tiempoRecuperacion)
                put("motivo", cirugia.motivo)
                put("procedimiento", cirugia.procedimiento)
                put("pronostico", cirugia.pronostico)
                put("complicaciones", cirugia.complicaciones)
                put("hospital", cirugia.hospital)
                put("quirofano", cirugia.quirofano)

                // Medicamentos de la cirugía
                val medicamentosJsonArray = JSONArray()
                cirugia.medicamentos.forEach { med ->
                    val medJson = JSONObject()
                    medJson.put("nombre", med.nombre)
                    medJson.put("dosis", med.dosis)
                    medJson.put("frecuencia", med.frecuencia)
                    medicamentosJsonArray.put(medJson)
                }
                put("medicamentos", medicamentosJsonArray)
            })
        }
        
        val jsonString = obj.toString()
        Log.i("WebSocketServer", "📤 Cirugía específica enviada")
        Log.i("WebSocketServer", "   - Tamaño JSON: ${jsonString.length} caracteres")
        Log.i("WebSocketServer", "   - Paciente: ${paciente.nombre} ${paciente.apellido}")
        Log.i("WebSocketServer", "   - Cirugía: ${cirugia.nombreCirugia}")
        Log.i("WebSocketServer", "   - Doctor: ${doctor?.nombre} ${doctor?.apellido}")
        
        if (tvConnection != null) {
            tvConnection?.send(jsonString)
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "Cirugía específica enviada a la TV", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.w("WebSocketServer", "Intento de enviar cirugía específica sin cliente conectado")
            context?.let {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(it, "No hay TV conectada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
