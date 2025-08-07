package com.tuempresa.medicalapp.core.constants

object AppConstants {
    // Roles de usuario
    const val ROLE_DOCTOR = "doctor"
    const val ROLE_SECRETARY = "secretaria"
    
    // Estados de autenticación
    const val AUTH_STATE_LOADING = "loading"
    const val AUTH_STATE_SUCCESS = "success"
    const val AUTH_STATE_ERROR = "error"
    
    // Nombres de pantallas
    const val SCREEN_LOGIN = "login"
    const val SCREEN_DOCTOR_MAIN = "doctor_main"
    const val SCREEN_SECRETARY_MAIN = "secretary_main"
    
    // Tamaños de UI
    const val DEFAULT_PADDING = 16
    const val DEFAULT_SPACING = 8
    const val CARD_ELEVATION = 4
    
    // Formatos de fecha
    const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMAT_DATABASE = "yyyy-MM-dd"
    
    // Límites de caracteres
    const val MAX_NAME_LENGTH = 50
    const val MAX_DESCRIPTION_LENGTH = 500
    const val MAX_PHONE_LENGTH = 15
} 