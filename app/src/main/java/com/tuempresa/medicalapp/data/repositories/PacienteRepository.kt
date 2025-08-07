package com.tuempresa.medicalapp.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.tuempresa.medicalapp.data.models.Paciente

class PacienteRepository {
    private val db = FirebaseFirestore.getInstance()
    private val pacientesRef = db.collection("pacientes")

    suspend fun agregarPaciente(paciente: Paciente): Result<String> {
        return try {
            val id = if (paciente.id.isEmpty()) pacientesRef.document().id else paciente.id
            
            // Limitar el tamaño de fotoBase64 si es muy grande
            val pacienteToSave = if (paciente.fotoBase64.length > 1000000) {
                paciente.copy(fotoBase64 = "") // Si es muy grande, no guardar la foto
            } else {
                paciente
            }
            
            pacientesRef.document(id).set(pacienteToSave.copy(id = id)).await()
            Result.success(id)
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error al agregar paciente: ", e)
            Result.failure(e)
        }
    }

    suspend fun actualizarPaciente(paciente: Paciente): Result<Unit> {
        return try {
            // Limitar el tamaño de fotoBase64 si es muy grande
            val pacienteToUpdate = if (paciente.fotoBase64.length > 1000000) {
                paciente.copy(fotoBase64 = "") // Si es muy grande, no guardar la foto
            } else {
                paciente
            }
            
            pacientesRef.document(paciente.id).set(pacienteToUpdate).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error al actualizar paciente: ", e)
            Result.failure(e)
        }
    }

    suspend fun eliminarPaciente(pacienteId: String): Result<Unit> {
        return try {
            // Eliminar consultas del paciente
            val consultasSnapshot = db.collection("consultas")
                .whereEqualTo("pacienteId", pacienteId)
                .get()
                .await()
            
            for (doc in consultasSnapshot.documents) {
                doc.reference.delete().await()
            }
            
            // Eliminar radiografías del paciente
            val radiografiasSnapshot = db.collection("radiografias")
                .whereEqualTo("pacienteId", pacienteId)
                .get()
                .await()
            
            for (doc in radiografiasSnapshot.documents) {
                doc.reference.delete().await()
            }
            
            // Eliminar cirugías del paciente
            val cirugiasSnapshot = db.collection("cirugias")
                .whereEqualTo("pacienteId", pacienteId)
                .get()
                .await()
            
            for (doc in cirugiasSnapshot.documents) {
                doc.reference.delete().await()
            }
            
            // Finalmente, eliminar el paciente
            pacientesRef.document(pacienteId).delete().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error al eliminar paciente y datos relacionados: ", e)
            Result.failure(e)
        }
    }

    // Dar de baja lógica a un paciente
    suspend fun darBajaPaciente(pacienteId: String): Result<Unit> {
        return try {
            pacientesRef.document(pacienteId).update("activo", false).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error al dar de baja paciente: ", e)
            Result.failure(e)
        }
    }

    // Reactivar un paciente
    suspend fun reactivarPaciente(pacienteId: String): Result<Unit> {
        return try {
            pacientesRef.document(pacienteId).update("activo", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error al reactivar paciente: ", e)
            Result.failure(e)
        }
    }

    suspend fun obtenerPacientes(): List<Paciente> {
        return try {
            val snapshot = pacientesRef
                .whereEqualTo("activo", true) // Solo pacientes activos por defecto
                .get()
                .await()
            val pacientes = mutableListOf<Paciente>()
            for (doc in snapshot.documents) {
                try {
                    val paciente = doc.toObject(Paciente::class.java)
                    if (paciente != null) {
                        pacientes.add(paciente.copy(id = doc.id))
                    }
                } catch (e: Exception) {
                    Log.e("PacienteRepository", "Error al leer paciente ${doc.id}: ", e)
                    // Ignora el registro mal formado
                }
            }
            pacientes
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error al obtener pacientes: ", e)
            emptyList()
        }
    }

    suspend fun obtenerPacientesPorDoctor(doctorId: String): List<Paciente> {
        return try {
            val snapshot = pacientesRef
                .whereEqualTo("idDoctorAsignado", doctorId)
                .whereEqualTo("activo", true) // Solo pacientes activos
                .get()
                .await()
            val pacientes = mutableListOf<Paciente>()
            for (doc in snapshot.documents) {
                try {
                    val paciente = doc.toObject(Paciente::class.java)
                    if (paciente != null) {
                        pacientes.add(paciente.copy(id = doc.id))
                    }
                } catch (e: Exception) {
                    Log.e("PacienteRepository", "Error al leer paciente ${doc.id}: ", e)
                }
            }
            pacientes
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error al obtener pacientes por doctor: ", e)
            emptyList()
        }
    }

    // Método para obtener todos los pacientes (activos e inactivos) - para administración
    suspend fun obtenerTodosLosPacientes(): List<Paciente> {
        return try {
            val snapshot = pacientesRef.get().await()
            val pacientes = mutableListOf<Paciente>()
            for (doc in snapshot.documents) {
                try {
                    val paciente = doc.toObject(Paciente::class.java)
                    if (paciente != null) {
                        pacientes.add(paciente.copy(id = doc.id))
                    }
                } catch (e: Exception) {
                    Log.e("PacienteRepository", "Error al leer paciente ${doc.id}: ", e)
                }
            }
            pacientes
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error al obtener todos los pacientes: ", e)
            emptyList()
        }
    }
} 