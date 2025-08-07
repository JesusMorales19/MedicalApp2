package com.tuempresa.medicalapp.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.tuempresa.medicalapp.data.models.Cirugia

class CirugiaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val cirugiasRef = db.collection("cirugias")

    suspend fun agregarCirugia(cirugia: Cirugia) {
        val id = if (cirugia.id.isEmpty()) cirugiasRef.document().id else cirugia.id
        cirugiasRef.document(id).set(cirugia.copy(id = id)).await()
    }

    suspend fun obtenerCirugiasPorPaciente(pacienteId: String): List<Cirugia> {
        try {
            val snapshot = cirugiasRef
                .whereEqualTo("pacienteId", pacienteId)
                .get()
                .await()
            return snapshot.documents.mapNotNull { doc ->
                doc.toObject(Cirugia::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            println("DEBUG: Error al obtener cirugías: ${e.message}")
            return emptyList()
        }
    }

    suspend fun obtenerTodasLasCirugias(): List<Cirugia> {
        try {
            val snapshot = cirugiasRef.get().await()
            return snapshot.documents.mapNotNull { doc ->
                doc.toObject(Cirugia::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            println("DEBUG: Error al obtener todas las cirugías: ${e.message}")
            return emptyList()
        }
    }
} 