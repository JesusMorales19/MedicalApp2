package com.tuempresa.medicalapp.data.repositories

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import com.tuempresa.medicalapp.data.models.Doctor

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class DoctorRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val doctorsCollection = db.collection("doctors")
    private val storage = FirebaseStorage.getInstance()

    // Registrar un nuevo doctor
    suspend fun registrarDoctor(doctor: Doctor, contrasena: String): AuthResult {
        return try {
            // Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(doctor.correo, contrasena).await()
            
            // Guardar datos adicionales en Firestore
            val doctorData = doctor.copy(
                id = authResult.user?.uid ?: "",
                contrasena = "" // No guardar contraseña en Firestore por seguridad
            )
            
            doctorsCollection.document(doctorData.id).set(doctorData).await()
            
            AuthResult.Success
        } catch (e: Exception) {
            when {
                e.message?.contains("email") == true -> AuthResult.Error("El correo ya está registrado")
                e.message?.contains("password") == true -> AuthResult.Error("La contraseña debe tener al menos 6 caracteres")
                else -> AuthResult.Error("Error al registrar: ${e.message}")
            }
        }
    }

    // Iniciar sesión
    suspend fun iniciarSesion(correo: String, contrasena: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(correo, contrasena).await()
            
            // Verificar que el doctor existe en Firestore y está activo
            val doctor = obtenerDoctorPorId(auth.currentUser?.uid ?: "")
            if (doctor == null) {
                auth.signOut()
                AuthResult.Error("Doctor no encontrado en la base de datos")
            } else if (!doctor.activo) {
                auth.signOut()
                AuthResult.Error("Cuenta desactivada")
            } else {
                AuthResult.Success
            }
        } catch (e: Exception) {
            when {
                e.message?.contains("no user record") == true -> AuthResult.Error("Correo no registrado")
                e.message?.contains("password is invalid") == true -> AuthResult.Error("Contraseña incorrecta")
                e.message?.contains("network") == true -> AuthResult.Error("Error de conexión")
                else -> AuthResult.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    // Cerrar sesión
    fun cerrarSesion() {
        auth.signOut()
    }

    // Obtener doctor actual
    fun obtenerDoctorActual(): Doctor? {
        val user = auth.currentUser
        return if (user != null) {
            // Aquí podrías cargar los datos completos desde Firestore
            Doctor(
                id = user.uid,
                correo = user.email ?: ""
            )
        } else null
    }

    // Obtener doctor por ID
    suspend fun obtenerDoctorPorId(doctorId: String): Doctor? {
        return try {
            val doc = doctorsCollection.document(doctorId).get().await()
            doc.toObject(Doctor::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar datos del doctor
    suspend fun actualizarDoctor(doctor: Doctor): AuthResult {
        return try {
            doctorsCollection.document(doctor.id).set(doctor).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Error al actualizar: ${e.message}")
        }
    }

    // Actualizar solo los campos editados del doctor
    suspend fun actualizarCamposDoctor(doctorId: String, campos: Map<String, Any?>): AuthResult {
        return try {
            doctorsCollection.document(doctorId).update(campos).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Error al actualizar: ${e.message}")
        }
    }

    // Verificar si hay sesión activa
    fun haySesionActiva(): Boolean {
        return auth.currentUser != null
    }

    // Obtener ID del usuario actual
    fun obtenerUsuarioActualId(): String? {
        return auth.currentUser?.uid
    }

    // Cargar datos iniciales de doctores (solo para desarrollo)
    suspend fun cargarDoctoresIniciales() {
        val doctorInicial = Doctor(
            nombre = "Ricardo Israel",
            apellido = "Gil Navarro",
            correo = "ricardo.gil@gmail.com",
            telefono = "6181562324",
            especialidad = "Cardiología",
            contrasena = "12345678",
            fotoUrl = "",
            activo = true,
            rol = "doctor"
        )

        val secretariaInicial = Doctor(
            nombre = "María",
            apellido = "González",
            correo = "secretaria@gmail.com",
            telefono = "6181234567",
            especialidad = "Administración",
            contrasena = "12345678",
            fotoUrl = "",
            activo = true,
            rol = "secretaria"
        )

        try {
            // Verificar si ya existe el doctor
            val existingDoctor = doctorsCollection
                .whereEqualTo("correo", doctorInicial.correo)
                .get()
                .await()

            if (existingDoctor.isEmpty) {
                registrarDoctor(doctorInicial, doctorInicial.contrasena)
            }

            // Verificar si ya existe la secretaria
            val existingSecretaria = doctorsCollection
                .whereEqualTo("correo", secretariaInicial.correo)
                .get()
                .await()

            if (existingSecretaria.isEmpty) {
                registrarDoctor(secretariaInicial, secretariaInicial.contrasena)
            }
        } catch (e: Exception) {
            // Ignorar errores en desarrollo
        }
    }

    // Subir imagen de perfil y actualizar URL en Firestore
    suspend fun actualizarFotoPerfil(doctorId: String, imageUri: Uri): Result<String> {
        return try {
            val ref = storage.reference.child("doctors/$doctorId/profile.jpg")
            ref.putFile(imageUri).await()
            val url = ref.downloadUrl.await().toString()
            // Actualizar campo fotoUrl en Firestore
            doctorsCollection.document(doctorId).update("fotoUrl", url).await()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todos los doctores (para la pantalla de secretaria)
    suspend fun obtenerTodosLosDoctores(): List<Doctor> {
        return try {
            val snapshot = doctorsCollection
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Doctor::class.java)?.copy(id = doc.id)
            }.filter { it.rol == "doctor" } // Solo doctores, no secretarias
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Dar de baja lógica a un doctor
    suspend fun darBajaDoctor(doctorId: String): AuthResult {
        return try {
            doctorsCollection.document(doctorId).update("activo", false).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Error al dar de baja: ${e.message}")
        }
    }

    // Reactivar un doctor
    suspend fun reactivarDoctor(doctorId: String): AuthResult {
        return try {
            doctorsCollection.document(doctorId).update("activo", true).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Error al reactivar: ${e.message}")
        }
    }

    // Eliminar completamente un doctor
    suspend fun eliminarDoctor(doctorId: String): AuthResult {
        return try {
            // Primero eliminar de Firebase Auth
            val user = auth.currentUser
            if (user?.uid == doctorId) {
                user.delete().await()
            }
            
            // Luego eliminar de Firestore
            doctorsCollection.document(doctorId).delete().await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Error al eliminar: ${e.message}")
        }
    }
} 