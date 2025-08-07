package com.tuempresa.medicalapp.core.utils

import com.tuempresa.medicalapp.core.constants.AppConstants
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtils {
    
    /**
     * Convierte un timestamp a formato legible dd/MM/yyyy
     */
    fun formatTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(AppConstants.DATE_FORMAT_DISPLAY, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Convierte una fecha a formato legible
     */
    fun formatDateToDisplay(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_DISPLAY)
        return date.format(formatter)
    }
    
    /**
     * Calcula la edad basada en la fecha de nacimiento
     */
    fun calculateAge(birthDate: LocalDate): Int {
        val today = LocalDate.now()
        return java.time.Period.between(birthDate, today).years
    }
    
    /**
     * Verifica si una fecha es válida
     */
    fun isValidDate(dateString: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_DISPLAY)
            LocalDate.parse(dateString, formatter)
            true
        } catch (e: Exception) {
            false
        }
    }
} 