package com.tuempresa.medicalapp.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.medicalapp.data.repositories.AuthResult
import com.tuempresa.medicalapp.data.models.Doctor
import com.tuempresa.medicalapp.data.repositories.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val doctor: Doctor? = null,
    val listaDoctores: List<Doctor> = emptyList()
)

class AuthViewModel : ViewModel() {
    private val repository = DoctorRepository()
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Verificar si hay sesión activa al iniciar
        verificarSesionActiva()
        // Cargar datos iniciales para desarrollo
        cargarDatosIniciales()
    }

    private fun verificarSesionActiva() {
        val haySesion = repository.haySesionActiva()
        val userId = repository.obtenerUsuarioActualId()
        viewModelScope.launch {
            val doctor = if (userId != null) repository.obtenerDoctorPorId(userId) else null
            _uiState.value = _uiState.value.copy(
                isAuthenticated = haySesion,
                doctor = doctor
            )
        }
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            repository.cargarDoctoresIniciales()
        }
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.iniciarSesion(correo, contrasena)) {
                is AuthResult.Success -> {
                    // Obtener el UID del usuario autenticado
                    val userId = repository.obtenerUsuarioActualId()
                    // Leer el documento completo del doctor desde Firestore
                    val doctor = if (userId != null) repository.obtenerDoctorPorId(userId) else null
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        doctor = doctor,
                        error = null
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false,
            doctor = null,
            error = null
        )
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun actualizarDoctor(doctor: Doctor, nuevaImagen: Uri? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                var doctorActualizado = doctor
                if (nuevaImagen != null && doctor.id.isNotEmpty()) {
                    val result = repository.actualizarFotoPerfil(doctor.id, nuevaImagen)
                    if (result.isSuccess) {
                        doctorActualizado = doctorActualizado.copy(fotoUrl = result.getOrNull() ?: "")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al subir la imagen: ${result.exceptionOrNull()?.message}"
                        )
                        return@launch
                    }
                }
                when (val result = repository.actualizarDoctor(doctorActualizado)) {
                    is AuthResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            doctor = doctorActualizado,
                            error = null
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al actualizar: ${e.message}"
                )
            }
        }
    }

    fun actualizarDoctorSoloCampos(doctorId: String, correo: String, telefono: String, nuevaImagen: Uri? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val campos = mutableMapOf<String, Any?>(
                    "correo" to correo,
                    "telefono" to telefono
                )
                if (nuevaImagen != null) {
                    val result = repository.actualizarFotoPerfil(doctorId, nuevaImagen)
                    if (result.isSuccess) {
                        campos["fotoUrl"] = result.getOrNull()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al subir la imagen: ${result.exceptionOrNull()?.message}"
                        )
                        return@launch
                    }
                }
                val result = repository.actualizarCamposDoctor(doctorId, campos)
                if (result is AuthResult.Success) {
                    // Refrescar el doctor desde Firestore para que siempre se muestre el nombre y los demás datos
                    val doctorActualizado = repository.obtenerDoctorPorId(doctorId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        doctor = doctorActualizado,
                        error = null
                    )
                } else if (result is AuthResult.Error) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al actualizar: ${e.message}"
                )
            }
        }
    }

    fun actualizarDoctorSoloCamposBase64(doctorId: String, correo: String, telefono: String, base64Image: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val campos = mutableMapOf<String, Any?>(
                    "correo" to correo,
                    "telefono" to telefono
                )
                if (base64Image != null) {
                    campos["fotoBase64"] = base64Image
                }
                val result = repository.actualizarCamposDoctor(doctorId, campos)
                if (result is AuthResult.Success) {
                    val doctorActualizado = repository.obtenerDoctorPorId(doctorId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        doctor = doctorActualizado,
                        error = null
                    )
                } else if (result is AuthResult.Error) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al actualizar: ${e.message}"
                )
            }
        }
    }

    fun actualizarFirmaDoctor(doctorId: String, firmaBase64: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val campos = mutableMapOf<String, Any?>(
                    "firmaBase64" to firmaBase64
                )
                val result = repository.actualizarCamposDoctor(doctorId, campos)
                if (result is AuthResult.Success) {
                    val doctorActualizado = repository.obtenerDoctorPorId(doctorId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        doctor = doctorActualizado,
                        error = null
                    )
                } else if (result is AuthResult.Error) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al actualizar la firma: ${e.message}"
                )
            }
        }
    }

    // Método para crear nuevo doctor desde la pantalla de secretaria
    fun crearNuevoDoctor(doctor: Doctor, contrasena: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.registrarDoctor(doctor, contrasena)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    // Recargar la lista de doctores después de crear uno nuevo
                    cargarListaDoctores()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    // Método para cargar la lista de doctores
    fun cargarListaDoctores() {
        viewModelScope.launch {
            try {
                val doctores = repository.obtenerTodosLosDoctores()
                _uiState.value = _uiState.value.copy(listaDoctores = doctores)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar la lista de doctores: ${e.message}"
                )
            }
        }
    }

    // Método para dar de baja lógica a un doctor
    fun darBajaDoctor(doctorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.darBajaDoctor(doctorId)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    cargarListaDoctores() // Recargar la lista
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    // Método para reactivar un doctor
    fun reactivarDoctor(doctorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.reactivarDoctor(doctorId)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    cargarListaDoctores() // Recargar la lista
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    // Método para eliminar completamente un doctor
    fun eliminarDoctor(doctorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.eliminarDoctor(doctorId)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    cargarListaDoctores() // Recargar la lista
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    // Método para registrar doctor desde el formulario
    fun registrarDoctor(doctor: Doctor) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.registrarDoctor(doctor, doctor.contrasena)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    // Recargar la lista de doctores después de crear uno nuevo
                    cargarListaDoctores()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
} 