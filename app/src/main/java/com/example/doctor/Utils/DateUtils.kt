package com.example.doctor.Utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    /**
     * Calcula la edad basada en la fecha de nacimiento
     * @param fechaNacimiento Formato: "dd/MM/yyyy"
     * @return Edad calculada en años
     */
    fun calcularEdad(fechaNacimiento: String): Int {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaNac = formato.parse(fechaNacimiento)
            val hoy = Calendar.getInstance()
            val fechaNacCal = Calendar.getInstance()
            fechaNacCal.time = fechaNac!!
            
            var edad = hoy.get(Calendar.YEAR) - fechaNacCal.get(Calendar.YEAR)
            
            // Verificar si ya cumplió años este año
            if (hoy.get(Calendar.DAY_OF_YEAR) < fechaNacCal.get(Calendar.DAY_OF_YEAR)) {
                edad--
            }
            
            edad
        } catch (e: Exception) {
            0 // Retorna 0 si hay error en el formato de fecha
        }
    }
    
    /**
     * Obtiene la fecha actual en formato timestamp
     * @return Timestamp actual en milisegundos
     */
    fun obtenerFechaCreacionTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Obtiene la fecha actual en formato legible
     * @return Fecha actual en formato "dd/MM/yyyy"
     */
    fun obtenerFechaCreacionString(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(Date())
    }
    
    /**
     * Obtiene la fecha y hora actual en formato legible
     * @return Fecha y hora actual en formato "dd/MM/yyyy HH:mm"
     */
    fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formato.format(Date())
    }
    
    /**
     * Valida si una fecha tiene el formato correcto
     * @param fecha Formato esperado: "dd/MM/yyyy"
     * @return true si el formato es válido
     */
    fun esFechaValida(fecha: String): Boolean {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.isLenient = false // No permite fechas inválidas
            formato.parse(fecha)
            true
        } catch (e: Exception) {
            false
        }
    }
} 