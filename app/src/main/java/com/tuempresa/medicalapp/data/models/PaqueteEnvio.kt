package com.tuempresa.medicalapp.data.models

// Modelo contenedor para enviar todos los datos a la TV

data class PaqueteEnvio(
    val doctor: Doctor, // Datos del doctor autenticado
    val paciente: Paciente,
    val consultas: List<Consulta>,
    val cirugias: List<Cirugia>,
    val radiografias: List<Radiografia>
) 