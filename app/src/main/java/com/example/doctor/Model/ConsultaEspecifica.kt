package com.example.doctor.Model

data class ConsultaEspecificaData(
    val tipo: String,
    val paciente: PacienteConsulta,
    val doctor: DoctorConsulta,
    val consulta: DetalleConsulta
)

data class PacienteConsulta(
    val id: String,
    val nombre: String,
    val apellido: String,
    val foto: String?,
    val tipoSangre: String,
    val sexo: String
)

data class DoctorConsulta(
    val id: String,
    val nombre: String,
    val apellido: String
)

data class DetalleConsulta(
    val id: String,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val duracionSintomas: String,
    val diagnosticoPrincipal: String,
    val diagnosticoSecundario: String,
    val enfermedadesCronicas: String,
    val alergias: String,
    val peso: Double,
    val altura: Double,
    val notas: String,
    val estudios: String,
    val indicaciones: String,
    val requiereSeguimiento: Boolean,
    val fechaProxima: String,
    val presionArterial: String,
    val temperatura: String,
    val frecuenciaCardiaca: String,
    val sintomas: List<String>,
    val medicamentos: List<MedicamentoConsulta>
)

data class MedicamentoConsulta(
    val nombre: String,
    val dosis: String,
    val frecuencia: String
) 