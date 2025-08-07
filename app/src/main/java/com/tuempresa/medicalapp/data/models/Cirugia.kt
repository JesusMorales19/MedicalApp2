package com.tuempresa.medicalapp.data.models

import java.util.UUID

data class Cirugia(
    val id: String = UUID.randomUUID().toString(),
    val nombreCirugia: String = "",
    val fecha: String = "", // formato dd/MM/yyyy
    val pacienteId: String = "",
    val doctorId: String = "",
    val nombreDoctor: String = "",
    val tipoProcesamiento: String = "",
    val tiempoRecuperacion: String = "",
    val motivo: String = "",
    val procedimiento: String = "",
    val pronostico: String = "",
    val complicaciones: String = "",
    val hospital: String ="",
    val quirofano: String = "",
    val medicamentos: List<Medicamento> = emptyList(),
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    // Constructor secundario sin argumentos para Firebase
    constructor() : this(
        id = "",
        nombreCirugia = "",
        fecha = "",
        pacienteId = "",
        doctorId = "",
            nombreDoctor = "",
        tipoProcesamiento = "",
        tiempoRecuperacion = "",
        motivo = "",
        procedimiento = "",
        pronostico = "",
        complicaciones = "",
        hospital = "",
        quirofano = "",
        medicamentos = emptyList(),
        fechaCreacion = 0L
    )
} 