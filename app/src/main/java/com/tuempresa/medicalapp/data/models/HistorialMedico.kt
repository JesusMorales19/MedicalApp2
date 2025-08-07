package com.tuempresa.medicalapp.data.models

data class HistorialMedico(
    val diagnosticoActual: String,
    val enfermedadesCronicas: String,
    val alergias: String,
    val cirugiasPrevias: String,
    val peso: Double, // en kg
    val altura: Double // en metros
)
