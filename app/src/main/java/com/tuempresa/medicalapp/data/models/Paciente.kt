package com.tuempresa.medicalapp.data.models

import java.time.LocalDate
import java.time.Period
import java.util.UUID
import com.tuempresa.medicalapp.data.models.Medicamento

data class Paciente(
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val curp: String = "",
    val fechaNacimiento: String = "", // formato yyyy-MM-dd
    val edad: Int = 0,
    val sexo: String = "",
    val telefono: String = "",
    val email: String = "",
    val direccion: String = "",
    val tipoSangre: String = "", // Nuevo campo para tipo de sangre
    val fotoBase64: String = "", // Foto del paciente en base64
    // Relación con doctor
    val idDoctorAsignado: String = "",
    val especialidadDoctor: String = "",
    val fechaCreacion: Long = 0L, // Fecha de creación en milisegundos
    val fechaCreacionStr: String = "", // Fecha de creación legible
    val activo: Boolean = true // Campo para baja lógica
) 