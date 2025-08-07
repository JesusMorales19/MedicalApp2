package com.example.doctor.Model

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log

data class DoctorData(
    val nombre: String,
    val apellido: String,
    val foto: String?
)

class TVDataViewModel : ViewModel() {

    // Para lista de pacientes
    var pacientesJson by mutableStateOf<JSONArray?>(null)
        private set

    // Para datos del doctor
    var doctorData by mutableStateOf<DoctorData?>(null)
        private set

    // Para detectar el tipo de mensaje
    var mensajeRecibido by mutableStateOf<String?>(null)
        private set

    // Timestamp de última actualización
    var ultimaActualizacion by mutableStateOf<Long>(0L)
        private set

    // Para radiografías
    var radiografiaData by mutableStateOf<RadiografiaData?>(null)
        private set
    
    var mostrarRadiografias by mutableStateOf(false)
        private set

    // Para consulta específica
    var consultaEspecificaData by mutableStateOf<ConsultaEspecificaData?>(null)
        private set
    
    var mostrarConsultaEspecifica by mutableStateOf(false)
        private set

    // Para cirugía específica
    var cirugiaEspecificaData by mutableStateOf<CirugiaEspecificaData?>(null)
        private set
    
    var mostrarCirugiaEspecifica by mutableStateOf(false)
        private set

    fun procesarMensajeWebSocket(mensaje: String) {
        Log.d("TVDataViewModel", "=== PROCESANDO MENSAJE WEBSOCKET ===")
        Log.d("TVDataViewModel", "Mensaje recibido: $mensaje")
        Log.d("TVDataViewModel", "Longitud del mensaje: ${mensaje.length} caracteres")
        try {
            val jsonObject = JSONObject(mensaje)
            val tipo = jsonObject.optString("tipo", "")
            Log.d("TVDataViewModel", "Tipo de mensaje: $tipo")
            Log.d("TVDataViewModel", "Claves disponibles en JSON: ${jsonObject.keys().asSequence().toList()}")
            
            when (tipo) {
                "datosDoctor" -> {
                    val doctorJson = jsonObject.getJSONObject("doctor")
                    val doctor = DoctorData(
                        nombre = doctorJson.optString("nombre", ""),
                        apellido = doctorJson.optString("apellido", ""),
                        foto = doctorJson.optString("foto", null)
                    )
                    doctorData = doctor
                    mensajeRecibido = "datosDoctor"
                    ultimaActualizacion = System.currentTimeMillis()
                }
                "datosPaciente" -> {
                    // Si es un JSONArray (datos de pacientes)
                    if (jsonObject.has("pacientes")) {
                        val pacientesArray = jsonObject.getJSONArray("pacientes")
                        actualizarPacientesJson(pacientesArray)
                        mensajeRecibido = "datosPaciente"
                        ultimaActualizacion = System.currentTimeMillis()
                    }
                }
                "radiografias" -> {
                    procesarRadiografias(jsonObject)
                    mensajeRecibido = "radiografias"
                    ultimaActualizacion = System.currentTimeMillis()
                }
                "consultaEspecifica" -> {
                    procesarConsultaEspecifica(jsonObject)
                    mensajeRecibido = "consultaEspecifica"
                    ultimaActualizacion = System.currentTimeMillis()
                }
                "cirugiaEspecifica" -> {
                    procesarCirugiaEspecifica(jsonObject)
                    mensajeRecibido = "cirugiaEspecifica"
                    ultimaActualizacion = System.currentTimeMillis()
                }
                else -> {
                    // Si no tiene tipo, asumimos que es directamente un JSONArray de pacientes
                    val jsonArray = JSONArray(mensaje)
                    actualizarPacientesJson(jsonArray)
                    mensajeRecibido = "datosPaciente"
                    ultimaActualizacion = System.currentTimeMillis()
                    Log.d("TVDataViewModel", "Procesado como JSONArray de pacientes")
                }
            }
            Log.d("TVDataViewModel", "Mensaje procesado exitosamente. Tipo final: $mensajeRecibido")
        } catch (e: Exception) {
            // Si falla el parsing como JSONObject, intentamos como JSONArray
            try {
                val jsonArray = JSONArray(mensaje)
                actualizarPacientesJson(jsonArray)
                mensajeRecibido = "datosPaciente"
            } catch (e2: Exception) {
                // Error en ambos casos
                mensajeRecibido = "error"
                ultimaActualizacion = System.currentTimeMillis()
            }
        }
    }

    private fun procesarRadiografias(jsonObject: JSONObject) {
        try {
            val tipoRadiografia = jsonObject.optString("tipoRadiografia", "")
            val pacienteJson = jsonObject.getJSONObject("paciente")
            val radiografiasArray = jsonObject.getJSONArray("radiografias")
            
            val paciente = PacienteRadiografia(
                nombre = pacienteJson.optString("nombre", ""),
                apellido = pacienteJson.optString("apellido", "")
            )
            
            val radiografias = mutableListOf<Radiografia>()
            for (i in 0 until radiografiasArray.length()) {
                val radioJson = radiografiasArray.getJSONObject(i)
                radiografias.add(
                    Radiografia(
                        fecha = radioJson.optString("fecha", ""),
                        imagenBase64 = radioJson.optString("imagenBase64", ""),
                        observaciones = radioJson.optString("observaciones", "")
                    )
                )
            }
            
            radiografiaData = RadiografiaData(
                tipoRadiografia = tipoRadiografia,
                paciente = paciente,
                radiografias = radiografias
            )
            
            mostrarRadiografias = true
        } catch (e: Exception) {
            // Error procesando radiografías
            mensajeRecibido = "error"
        }
    }

    private fun procesarConsultaEspecifica(jsonObject: JSONObject) {
        try {
            val pacienteJson = jsonObject.getJSONObject("paciente")
            val doctorJson = jsonObject.getJSONObject("doctor")
            val consultaJson = jsonObject.getJSONObject("consulta")

            val paciente = PacienteConsulta(
                id = pacienteJson.optString("id", ""),
                nombre = pacienteJson.optString("nombre", ""),
                apellido = pacienteJson.optString("apellido", ""),
                foto = pacienteJson.optString("foto", null),
                tipoSangre = pacienteJson.optString("tipoSangre", ""),
                sexo = pacienteJson.optString("sexo", "")
            )
            
            val doctor = DoctorConsulta(
                id = doctorJson.optString("id", ""),
                nombre = doctorJson.optString("nombre", ""),
                apellido = doctorJson.optString("apellido", "")
            )
            
            // Procesar síntomas
            val sintomasJson = consultaJson.optJSONArray("sintomas") ?: org.json.JSONArray()
            val sintomas = mutableListOf<String>()
            for (i in 0 until sintomasJson.length()) {
                sintomas.add(sintomasJson.optString(i, ""))
            }
            
            // Procesar medicamentos
            val medicamentosJson = consultaJson.optJSONArray("medicamentos") ?: org.json.JSONArray()
            val medicamentos = mutableListOf<MedicamentoConsulta>()
            for (i in 0 until medicamentosJson.length()) {
                val medJson = medicamentosJson.getJSONObject(i)
                medicamentos.add(
                    MedicamentoConsulta(
                        nombre = medJson.optString("nombre", ""),
                        dosis = medJson.optString("dosis", ""),
                        frecuencia = medJson.optString("frecuencia", "")
                    )
                )
            }
            
            val consulta = DetalleConsulta(
                id = consultaJson.optString("id", ""),
                fecha = consultaJson.optString("fecha", ""),
                hora = consultaJson.optString("hora", ""),
                motivo = consultaJson.optString("motivo", ""),
                duracionSintomas = consultaJson.optString("duracionSintomas", ""),
                diagnosticoPrincipal = consultaJson.optString("diagnosticoPrincipal", ""),
                diagnosticoSecundario = consultaJson.optString("diagnosticoSecundario", ""),
                enfermedadesCronicas = consultaJson.optString("enfermedadesCronicas", ""),
                alergias = consultaJson.optString("alergias", ""),
                peso = consultaJson.optDouble("peso", 0.0),
                altura = consultaJson.optDouble("altura", 0.0),
                notas = consultaJson.optString("notas", ""),
                estudios = consultaJson.optString("estudios", ""),
                indicaciones = consultaJson.optString("indicaciones", ""),
                requiereSeguimiento = consultaJson.optBoolean("requiereSeguimiento", false),
                fechaProxima = consultaJson.optString("fechaProxima", ""),
                presionArterial = consultaJson.optString("presionArterial", ""),
                temperatura = consultaJson.optString("temperatura", ""),
                frecuenciaCardiaca = consultaJson.optString("frecuenciaCardiaca", ""),
                sintomas = sintomas,
                medicamentos = medicamentos
            )
            
            consultaEspecificaData = ConsultaEspecificaData(
                tipo = jsonObject.optString("tipo", ""),
                paciente = paciente,
                doctor = doctor,
                consulta = consulta
            )
            
            mostrarConsultaEspecifica = true
        } catch (e: Exception) {
            // Error procesando consulta específica
            mensajeRecibido = "error"
        }
    }

    fun actualizarPacientesJson(json: JSONArray) {
        pacientesJson = json
    }

    // ✅ NUEVO: Para el expediente clínico seleccionado
    var expedienteData by mutableStateOf<ExpedienteData?>(null)
        private set

    fun actualizarExpedienteData(expediente: ExpedienteData) {
        expedienteData = expediente
    }

    fun clearExpedienteData() {
        expedienteData = null
    }

    fun clearDoctorData() {
        doctorData = null
        mensajeRecibido = null
        ultimaActualizacion = 0L
    }

    // Funciones para manejar radiografías
    fun cerrarRadiografias() {
        mostrarRadiografias = false
        radiografiaData = null
    }

    fun limpiarRadiografias() {
        radiografiaData = null
        mostrarRadiografias = false
    }

    // Funciones para manejar consulta específica
    fun cerrarConsultaEspecifica() {
        mostrarConsultaEspecifica = false
        consultaEspecificaData = null
    }

    fun limpiarConsultaEspecifica() {
        consultaEspecificaData = null
        mostrarConsultaEspecifica = false
    }

    private fun procesarCirugiaEspecifica(jsonObject: JSONObject) {
        try {
            val pacienteJson = jsonObject.getJSONObject("paciente")
            val doctorJson = jsonObject.getJSONObject("doctor")
            val cirugiaJson = jsonObject.getJSONObject("cirugia")

            val paciente = PacienteCirugia(
                id = pacienteJson.optString("id", ""),
                nombre = pacienteJson.optString("nombre", ""),
                apellido = pacienteJson.optString("apellido", ""),
                foto = pacienteJson.optString("foto", null)
            )
            
            val doctor = DoctorCirugia(
                id = doctorJson.optString("id", ""),
                nombre = doctorJson.optString("nombre", ""),
                apellido = doctorJson.optString("apellido", ""),
                especialidad = doctorJson.optString("especialidad", ""),
                foto = doctorJson.optString("foto", null)
            )
            
            // Procesar medicamentos
            val medicamentosJson = cirugiaJson.optJSONArray("medicamentos") ?: org.json.JSONArray()
            val medicamentos = mutableListOf<MedicamentoCirugia>()
            for (i in 0 until medicamentosJson.length()) {
                val medJson = medicamentosJson.getJSONObject(i)
                medicamentos.add(
                    MedicamentoCirugia(
                        nombre = medJson.optString("nombre", ""),
                        dosis = medJson.optString("dosis", ""),
                        frecuencia = medJson.optString("frecuencia", "")
                    )
                )
            }
            
            val cirugia = DetalleCirugia(
                id = cirugiaJson.optString("id", ""),
                nombreCirugia = cirugiaJson.optString("nombreCirugia", ""),
                fecha = cirugiaJson.optString("fecha", ""),
                nombreDoctor = cirugiaJson.optString("nombreDoctor", ""),
                tipoProcesamiento = cirugiaJson.optString("tipoProcesamiento", ""),
                tiempoRecuperacion = cirugiaJson.optString("tiempoRecuperacion", ""),
                motivo = cirugiaJson.optString("motivo", ""),
                procedimiento = cirugiaJson.optString("procedimiento", ""),
                pronostico = cirugiaJson.optString("pronostico", ""),
                complicaciones = cirugiaJson.optString("complicaciones", ""),
                hospital = cirugiaJson.optString("hospital", ""),
                quirofano = cirugiaJson.optString("quirofano", ""),
                medicamentos = medicamentos
            )
            
            cirugiaEspecificaData = CirugiaEspecificaData(
                tipo = jsonObject.optString("tipo", ""),
                paciente = paciente,
                doctor = doctor,
                cirugia = cirugia
            )
            
            mostrarCirugiaEspecifica = true
        } catch (e: Exception) {
            // Error procesando cirugía específica
            mensajeRecibido = "error"
        }
    }

    // Funciones para manejar cirugía específica
    fun cerrarCirugiaEspecifica() {
        mostrarCirugiaEspecifica = false
        cirugiaEspecificaData = null
    }

    fun limpiarCirugiaEspecifica() {
        cirugiaEspecificaData = null
        mostrarCirugiaEspecifica = false
    }
}
