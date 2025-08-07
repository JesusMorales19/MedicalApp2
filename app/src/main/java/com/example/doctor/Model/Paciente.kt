package com.example.doctor.Model

data class ExpedienteData(
    val nombreCompleto: String,
    val edad: String,
    val sexo: String,
    val tipoSangre: String,
    val fotoBase64: String?,
    val fechaNacimiento: String,
    val telefono: String,
    val direccion: String,
    val curp: String,
    val email: String,
    val ultimaConsultaFecha: String,
    val diagnosticoActual: String,
    val alergias: String,
    val enfermedadesCronicas: String,
    val cirugiasPrevias: String,
    val peso: String,
    val altura: String,
    val imc: String,
    val medicamentos: List<Medicamento> = emptyList(),
    val consultas: List<Consulta>,
    val notasDoctor: String
)

data class Medicamento(
    val nombre: String,
    val dosis: String,
    val frecuencia: String
)

data class Consulta(
    val fecha: String,
    val motivo: String
)

// Nuevos modelos para radiografías
data class RadiografiaData(
    val tipoRadiografia: String,
    val paciente: PacienteRadiografia,
    val radiografias: List<Radiografia>
)

data class PacienteRadiografia(
    val nombre: String,
    val apellido: String
)

data class Radiografia(
    val fecha: String,
    val imagenBase64: String,
    val observaciones: String
)

// Modelos para cirugía específica
data class CirugiaEspecificaData(
    val tipo: String,
    val paciente: PacienteCirugia,
    val doctor: DoctorCirugia,
    val cirugia: DetalleCirugia
)

data class PacienteCirugia(
    val id: String,
    val nombre: String,
    val apellido: String,
    val foto: String?
)

data class DoctorCirugia(
    val id: String,
    val nombre: String,
    val apellido: String,
    val especialidad: String,
    val foto: String?
)

data class DetalleCirugia(
    val id: String,
    val nombreCirugia: String,
    val fecha: String,
    val nombreDoctor: String,
    val tipoProcesamiento: String,
    val tiempoRecuperacion: String,
    val motivo: String,
    val procedimiento: String,
    val pronostico: String,
    val complicaciones: String,
    val hospital: String,
    val quirofano: String,
    val medicamentos: List<MedicamentoCirugia>
)

data class MedicamentoCirugia(
    val nombre: String,
    val dosis: String,
    val frecuencia: String
)

