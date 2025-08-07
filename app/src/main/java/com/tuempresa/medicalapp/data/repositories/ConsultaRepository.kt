package com.tuempresa.medicalapp.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.tuempresa.medicalapp.data.models.Consulta

class ConsultaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val consultasRef = db.collection("consultas")

    suspend fun agregarConsulta(consulta: Consulta) {
        val id = if (consulta.id.isEmpty()) consultasRef.document().id else consulta.id
        consultasRef.document(id).set(consulta.copy(id = id)).await()
    }

    suspend fun actualizarConsulta(consulta: Consulta) {
        if (consulta.id.isNotEmpty()) {
            consultasRef.document(consulta.id).set(consulta).await()
        }
    }

    suspend fun obtenerConsultasPorPaciente(pacienteId: String): List<Consulta> {
        println("DEBUG: Buscando consultas para pacienteId: $pacienteId")
        
        try {
            val snapshot = consultasRef
                .whereEqualTo("pacienteId", pacienteId)
                // .orderBy("fecha, com.google.firebase.firestore.Query.Direction.DESCENDING) // Comentado temporalmente
                .get()
                .await()
            
            println("DEBUG: Encontrados ${snapshot.documents.size} documentos")
            
            val consultas = snapshot.documents.mapNotNull { doc ->
                println("DEBUG: Procesando documento: ${doc.id}")
                doc.toObject(Consulta::class.java)?.copy(id = doc.id)
            }
            
            println("DEBUG: Consultas procesadas: ${consultas.size}")
            consultas.forEach { consulta ->
                println("DEBUG: Consulta - ID: ${consulta.id}, Motivo: ${consulta.motivo}, Fecha: ${consulta.fecha}")
            }
            
            return consultas
        } catch (e: Exception) {
            println("DEBUG: Error al obtener consultas: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    // Función de depuración para ver todas las consultas
    suspend fun obtenerTodasLasConsultas(): List<Consulta> {
        println("DEBUG: Obteniendo TODAS las consultas de la base de datos")
        
        try {
            val snapshot = consultasRef.get().await()
            println("DEBUG: Total de documentos en colección consultas: ${snapshot.documents.size}")
            
            snapshot.documents.forEach { doc ->
                println("DEBUG: Documento ID: ${doc.id}")
                println("DEBUG: Documento datos: ${doc.data}")
                
                val consulta = doc.toObject(Consulta::class.java)
                if (consulta != null) {
                    println("DEBUG: Consulta deserializada - ID: ${consulta.id}, PacienteId: ${consulta.pacienteId}, Motivo: ${consulta.motivo}")
                } else {
                    println("DEBUG: Error al deserializar documento ${doc.id}")
                }
            }
            
            val consultas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Consulta::class.java)?.copy(id = doc.id)
            }
            
            println("DEBUG: Total de consultas deserializadas: ${consultas.size}")
            return consultas
        } catch (e: Exception) {
            println("DEBUG: Error al obtener todas las consultas: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }
} 