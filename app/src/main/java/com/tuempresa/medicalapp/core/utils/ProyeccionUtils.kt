package com.tuempresa.medicalapp.core.utils

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.tuempresa.medicalapp.data.models.PaqueteEnvio
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.models.Radiografia
import com.tuempresa.medicalapp.data.models.Consulta
import com.tuempresa.medicalapp.core.network.ProyeccionService
import java.io.File
import com.tuempresa.medicalapp.data.models.Doctor
import com.tuempresa.medicalapp.data.models.Cirugia

object ProyeccionUtils {
    fun enviarPaquete(context: Context, paquete: PaqueteEnvio) {
        val json = Gson().toJson(paquete)
        val file = File(context.cacheDir, "paquete_envio.json")
        file.writeText(json)
        val intent = Intent(context, ProyeccionService::class.java)
        intent.putExtra("paqueteFilePath", file.absolutePath)
        context.startService(intent)
    }
    
    fun enviarDatosDoctor(
        context: Context,
        nombreDoctor: String,
        apellidoDoctor: String,
        fotoDoctor: String
    ) {
        val intent = Intent(context, ProyeccionService::class.java)
        intent.putExtra("nombreDoctor", nombreDoctor)
        intent.putExtra("apellidoDoctor", apellidoDoctor)
        intent.putExtra("fotoDoctor", fotoDoctor)
        context.startService(intent)
    }

    fun enviarRadiografiasPorTipo(
        context: Context,
        paciente: Paciente,
        radiografias: List<Radiografia>,
        tipoRadiografia: String
    ) {
        val intent = Intent(context, ProyeccionService::class.java)
        intent.putExtra("tipoEnvio", "radiografias")
        intent.putExtra("tipoRadiografia", tipoRadiografia)
        intent.putExtra("pacienteNombre", paciente.nombre)
        intent.putExtra("pacienteApellido", paciente.apellido)
        
        // Guardar radiografías filtradas en archivo temporal
        val radiografiasFiltradas = radiografias.filter { it.tipo.equals(tipoRadiografia, ignoreCase = true) }
        val json = Gson().toJson(radiografiasFiltradas)
        val file = File(context.cacheDir, "radiografias_${tipoRadiografia.lowercase()}.json")
        file.writeText(json)
        intent.putExtra("radiografiasFilePath", file.absolutePath)
        
        context.startService(intent)
    }

    fun enviarConsultaEspecifica(
        context: Context,
        paciente: Paciente,
        consulta: Consulta,
        doctor: Doctor? = null
    ) {
        val intent = Intent(context, ProyeccionService::class.java)
        intent.putExtra("tipoEnvio", "consultaEspecifica")
        intent.putExtra("pacienteNombre", paciente.nombre)
        intent.putExtra("pacienteApellido", paciente.apellido)
        intent.putExtra("pacienteId", paciente.id)
        intent.putExtra("pacienteSexo", paciente.sexo)
        intent.putExtra("pacienteTipoSangre", paciente.tipoSangre)
        
        // Comprimir foto del paciente
        val fotoPacienteComprimida = if (paciente.fotoBase64.isNotEmpty()) {
            ImageUtils.compressBase64Image(paciente.fotoBase64, 400) // 400KB para paciente
        } else ""
        intent.putExtra("pacienteFoto", fotoPacienteComprimida)
        
        // Datos del médico si están disponibles
        if (doctor != null) {
            intent.putExtra("nombreDoctor", doctor.nombre)
            intent.putExtra("apellidoDoctor", doctor.apellido)
            intent.putExtra("especialidadDoctor", doctor.especialidad)
            intent.putExtra("fotoDoctor", doctor.fotoBase64)
        }
        
        // Guardar consulta en archivo temporal
        val json = Gson().toJson(consulta)
        val file = File(context.cacheDir, "consulta_especifica.json")
        file.writeText(json)
        intent.putExtra("consultaFilePath", file.absolutePath)
        
        context.startService(intent)
    }

    fun enviarCirugiaEspecifica(
        context: Context,
        paciente: Paciente,
        cirugia: Cirugia,
        doctor: Doctor? = null
    ) {
        val intent = Intent(context, ProyeccionService::class.java)
        intent.putExtra("tipoEnvio", "cirugiaEspecifica")
        intent.putExtra("pacienteNombre", paciente.nombre)
        intent.putExtra("pacienteApellido", paciente.apellido)
        intent.putExtra("pacienteId", paciente.id)
        
        // Comprimir foto del paciente (más importante, comprimir menos)
        val fotoPacienteComprimida = if (paciente.fotoBase64.isNotEmpty()) {
            ImageUtils.compressBase64Image(paciente.fotoBase64, 400) // 400KB para paciente
        } else ""
        intent.putExtra("pacienteFoto", fotoPacienteComprimida)
        
        // Solo datos básicos del médico (sin foto ni especialidad)
        if (doctor != null) {
            intent.putExtra("nombreDoctor", doctor.nombre)
            intent.putExtra("apellidoDoctor", doctor.apellido)
        }
        
        // Guardar cirugía en archivo temporal
        val json = Gson().toJson(cirugia)
        val file = File(context.cacheDir, "cirugia_especifica.json")
        file.writeText(json)
        intent.putExtra("cirugiaFilePath", file.absolutePath)
        
        context.startService(intent)
    }
} 