package com.tuempresa.medicalapp.data.models

import com.google.firebase.firestore.DocumentId

data class Doctor(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val telefono: String = "",
    val especialidad: String = "",
    val contrasena: String = "",
    val consultorio: String = "",
    val matricula: String = "",
    val cedulaProfesional: String = "",
    val fotoUrl: String = "",
    val fotoBase64: String = "",
    val firmaBase64: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val activo: Boolean = true,
    val rol: String = "doctor" // "doctor" o "secretaria"
) 