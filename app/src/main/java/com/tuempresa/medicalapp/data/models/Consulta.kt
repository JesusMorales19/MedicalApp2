package com.tuempresa.medicalapp.data.models

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class Consulta(
    val id: String = UUID.randomUUID().toString(),
    val pacienteId: String,
    val doctorId: String,
    val motivo: String,
    val fecha: String, // formato dd/MM/yyyy
    val hora: String, // formato HH:mm
    val sintomas: List<String> = emptyList(), // Lista de síntomas separados
    val duracionSintomas: String = "", // Duración de síntomas en días
    val diagnosticoPrincipal: String = "",
    val diagnosticoSecundario: String = "",
    val medicamentos: List<Medicamento> = emptyList(),
    val estudios: String = "",
    val indicaciones: String = "",
    val requiereSeguimiento: Boolean = false,
    val fechaProxima: String = "",
    val notas: String = "",
    val presionArterial: String = "",
    val temperatura: String = "",
    val frecuenciaCardiaca: String = "",
    val peso: Double = 0.0,
    val altura: Double = 0.0,
    val alergias: String = "",
    val enfermedadesCronicas: String = ""
) {
    // Constructor secundario sin argumentos para Firebase
    constructor() : this(
        id = "",
        pacienteId = "",
        doctorId = "",
        motivo = "",
        fecha = "",
        hora = "",
        sintomas = emptyList(),
        duracionSintomas = "",
        diagnosticoPrincipal = "",
        diagnosticoSecundario = "",
        medicamentos = emptyList(),
        estudios = "",
        indicaciones = "",
        requiereSeguimiento = false,
        fechaProxima = "",
        notas = "",
        presionArterial = "",
        temperatura = "",
        frecuenciaCardiaca = "",
        peso = 0.0,
        altura = 0.0,
        alergias = "",
        enfermedadesCronicas = ""
    )
}
