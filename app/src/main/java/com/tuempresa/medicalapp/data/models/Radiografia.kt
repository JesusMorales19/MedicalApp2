package com.tuempresa.medicalapp.data.models

import java.util.UUID

// Modelo de datos para radiografía

data class Radiografia(
    val id: String = UUID.randomUUID().toString(),
    val pacienteId: String = "",
    val tipo: String = "", // Tórax, Extremidades, Columna, etc.
    val fecha: String = "", // formato dd/MM/yyyy
    val imagenBase64: String = "",
    val observaciones: String = "",
    val medicoId: String = ""
)
