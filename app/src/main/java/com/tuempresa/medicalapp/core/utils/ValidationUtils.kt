package com.tuempresa.medicalapp.core.utils

import java.util.regex.Pattern

object ValidationUtils {
    
    // Validación de email
    fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )
        return emailPattern.matcher(email).matches()
    }
    
    // Validación de teléfono (formato mexicano)
    fun isValidPhone(phone: String): Boolean {
        val phonePattern = Pattern.compile("^[0-9]{10}$")
        return phonePattern.matcher(phone.replace("\\s".toRegex(), "")).matches()
    }
    
    // Validación de CURP
    fun isValidCURP(curp: String): Boolean {
        val curpPattern = Pattern.compile("^[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[0-9A-Z][0-9]$")
        return curpPattern.matcher(curp.uppercase()).matches()
    }
    
    // Validación de fecha de nacimiento (formato DD/MM/YYYY)
    fun isValidDate(date: String): Boolean {
        val datePattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/(19|20)\\d\\d$")
        return datePattern.matcher(date).matches()
    }
    
    // Validación de longitud mínima
    fun hasMinLength(text: String, minLength: Int): Boolean {
        return text.length >= minLength
    }
    
    // Validación de longitud máxima
    fun hasMaxLength(text: String, maxLength: Int): Boolean {
        return text.length <= maxLength
    }
    
    // Validación de campo no vacío
    fun isNotEmpty(text: String): Boolean {
        return text.trim().isNotEmpty()
    }
    
    // Validación de solo letras
    fun isOnlyLetters(text: String): Boolean {
        val lettersPattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
        return lettersPattern.matcher(text).matches()
    }
    
    // Validación de solo números
    fun isOnlyNumbers(text: String): Boolean {
        val numbersPattern = Pattern.compile("^[0-9]+$")
        return numbersPattern.matcher(text).matches()
    }
    
    // Validación de peso (entre 0.1 y 500 kg)
    fun isValidWeight(weight: String): Boolean {
        val weightValue = weight.toDoubleOrNull()
        return weightValue != null && weightValue > 0.1 && weightValue <= 500
    }
    
    // Validación de altura (entre 0.1 y 3 metros)
    fun isValidHeight(height: String): Boolean {
        val heightValue = height.toDoubleOrNull()
        return heightValue != null && heightValue > 0.1 && heightValue <= 3
    }
    
    // Validación de presión arterial (formato: 120/80)
    fun isValidBloodPressure(pressure: String): Boolean {
        val pressurePattern = Pattern.compile("^[0-9]{2,3}/[0-9]{2,3}$")
        if (!pressurePattern.matcher(pressure).matches()) return false
        
        val parts = pressure.split("/")
        val systolic = parts[0].toIntOrNull()
        val diastolic = parts[1].toIntOrNull()
        
        return systolic != null && diastolic != null && 
               systolic in 70..250 && diastolic in 40..150
    }
    
    // Validación de temperatura (entre 30 y 45 grados Celsius)
    fun isValidTemperature(temperature: String): Boolean {
        val tempValue = temperature.toDoubleOrNull()
        return tempValue != null && tempValue >= 30 && tempValue <= 45
    }
    
    // Validación de frecuencia cardíaca (entre 40 y 200 bpm)
    fun isValidHeartRate(rate: String): Boolean {
        val rateValue = rate.toIntOrNull()
        return rateValue != null && rateValue >= 40 && rateValue <= 200
    }
    
    // Validación de contraseña
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
    
    // Validación de contraseña con requisitos específicos
    fun isStrongPassword(password: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        
        return hasMinLength && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }
    
    // Obtener mensajes de error específicos para contraseña
    fun getPasswordErrorMessage(password: String): String {
        return when {
            password.isEmpty() -> "La contraseña es obligatoria"
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            !password.any { it.isUpperCase() } -> "La contraseña debe contener al menos una mayúscula"
            !password.any { it.isLowerCase() } -> "La contraseña debe contener al menos una minúscula"
            !password.any { it.isDigit() } -> "La contraseña debe contener al menos un número"
            !password.any { !it.isLetterOrDigit() } -> "La contraseña debe contener al menos un carácter especial"
            else -> ""
        }
    }

    // Obtener mensaje de error para validación
    fun getErrorMessage(fieldName: String, validationType: String): String {
        return when (validationType) {
            "required" -> "$fieldName es obligatorio"
            "email" -> "Formato de email inválido"
            "phone" -> "Formato de teléfono inválido (10 dígitos)"
            "curp" -> "Formato de CURP inválido"
            "date" -> "Formato de fecha inválido (DD/MM/YYYY)"
            "minLength" -> "$fieldName debe tener al menos 3 caracteres"
            "maxLength" -> "$fieldName no puede exceder 50 caracteres"
            "letters" -> "$fieldName solo puede contener letras"
            "numbers" -> "$fieldName solo puede contener números"
            "weight" -> "Peso debe estar entre 0.1 y 500 kg"
            "height" -> "Altura debe estar entre 0.1 y 3 metros"
            "bloodPressure" -> "Formato de presión arterial inválido (ej: 120/80)"
            "temperature" -> "Temperatura debe estar entre 30 y 45°C"
            "heartRate" -> "Frecuencia cardíaca debe estar entre 40 y 200 bpm"
            "password" -> "La contraseña debe tener al menos 8 caracteres"
            else -> "Campo inválido"
        }
    }
} 