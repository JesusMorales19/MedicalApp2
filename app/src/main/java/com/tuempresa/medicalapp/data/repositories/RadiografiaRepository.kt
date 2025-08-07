package com.tuempresa.medicalapp.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.tuempresa.medicalapp.data.models.Radiografia

class RadiografiaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val radiografiasRef = db.collection("radiografias")

    suspend fun agregarRadiografia(radiografia: Radiografia) {
        val id = if (radiografia.id.isEmpty()) radiografiasRef.document().id else radiografia.id
        radiografiasRef.document(id).set(radiografia.copy(id = id)).await()
    }

    suspend fun obtenerRadiografiasPorPaciente(pacienteId: String): List<Radiografia> {
        val snapshot = radiografiasRef.whereEqualTo("pacienteId", pacienteId).get().await()
        return snapshot.documents.mapNotNull { it.toObject(Radiografia::class.java) }
    }
} 