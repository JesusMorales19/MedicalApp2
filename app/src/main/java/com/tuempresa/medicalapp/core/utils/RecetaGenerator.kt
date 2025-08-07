package com.tuempresa.medicalapp.core.utils

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument.PageInfo
import android.graphics.pdf.PdfDocument.Page
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Path
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.util.Base64
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.tuempresa.medicalapp.data.models.Consulta
import com.tuempresa.medicalapp.data.models.Doctor
import com.tuempresa.medicalapp.data.models.Paciente
import com.tuempresa.medicalapp.data.models.Medicamento

class RecetaGenerator {
    
    companion object {
        fun generarRecetaPDF(
            context: Context,
            consulta: Consulta,
            doctor: Doctor,
            paciente: Paciente
        ): File {
            val pdfDocument = PdfDocument()
            
            // Dimensiones de 22x12 cm convertidas a puntos (1 cm = 28.35 puntos)
            val widthPts = (22 * 28.35).toInt()  // 623 puntos
            val heightPts = (12 * 28.35).toInt() // 340 puntos
            
            // Calcular cuántas páginas necesitamos (máximo 4 medicamentos por página)
            val medicamentosPorPagina = 4
            val totalMedicamentos = consulta.medicamentos.size
            val totalPaginas = if (totalMedicamentos <= medicamentosPorPagina) 1 else 
                ((totalMedicamentos - medicamentosPorPagina) / medicamentosPorPagina) + 1
            
            // Crear las páginas
            for (pagina in 1..totalPaginas) {
                val pageInfo = PageInfo.Builder(widthPts, heightPts, pagina).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                
                dibujarPaginaReceta(canvas, context, consulta, doctor, paciente, pagina, totalPaginas, medicamentosPorPagina)
                
                pdfDocument.finishPage(page)
            }
            
            // Guardar el archivo
            val fileName = "Receta_${paciente.nombre}_${paciente.apellido}_${consulta.fecha.replace("/", "_")}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            
            return file
        }
        
        private fun dibujarPaginaReceta(
            canvas: Canvas,
            context: Context,
            consulta: Consulta,
            doctor: Doctor,
            paciente: Paciente,
            paginaActual: Int,
            totalPaginas: Int,
            medicamentosPorPagina: Int
        ) {
            val paint = Paint()
            paint.isAntiAlias = true
            
            // Configuración de colores
            val colorNegro = Color.BLACK
            val colorAzul = Color.rgb(0, 100, 200)
            val colorGris = Color.rgb(128, 128, 128)
            val colorGrisClaro = Color.rgb(200, 200, 200)
            val colorBlanco = Color.WHITE
            
            // Fondo blanco
            canvas.drawColor(colorBlanco)
            
            var yPosition = 30f
            val marginLeft = 30f
            val marginRight = 593f // 623 - 30
            
            // ===== HEADER =====
            // Logo de la app (ic_medical_app)
            try {
                val logoBitmap = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier("ic_medical_logo", "drawable", context.packageName))
                if (logoBitmap != null) {
                    val logoSize = 60f // Logo más grande
                    val logoRect = Rect(0, 0, logoBitmap.width, logoBitmap.height)
                    val destRect = RectF(marginLeft, yPosition - 25f, marginLeft + logoSize, yPosition + 35f)
                    canvas.drawBitmap(logoBitmap, logoRect, destRect, paint)
                    logoBitmap.recycle()
                } else {
                    // Fallback si no encuentra el logo
                    paint.apply {
                        color = colorNegro
                        textSize = 40f // Texto más grande para el fallback
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        textAlign = Paint.Align.LEFT
                    }
                    canvas.drawText("🏥", marginLeft, yPosition + 15f, paint)
                }
            } catch (e: Exception) {
                // Fallback si hay error
                paint.apply {
                    color = colorNegro
                    textSize = 40f // Texto más grande para el fallback
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.LEFT
                }
                canvas.drawText("🏥", marginLeft, yPosition + 15f, paint)
            }
            
            // Nombre del doctor centrado
            paint.apply {
                textSize = 16f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("DR. ${doctor.nombre} ${doctor.apellido}", 311.5f, yPosition, paint)
            
            yPosition += 20f
            
            // Especialidad del doctor
            paint.apply {
                textSize = 12f
                typeface = Typeface.DEFAULT
            }
            canvas.drawText("Especialidad: ${doctor.especialidad}", 311.5f, yPosition, paint)
            
            yPosition += 15f
            
            // Cédula del doctor
            canvas.drawText("Cédula: ${doctor.cedulaProfesional}", 311.5f, yPosition, paint)
            
            // Línea separadora
            yPosition += 20f
            paint.apply {
                color = colorNegro
                strokeWidth = 2f
                style = Paint.Style.STROKE
            }
            canvas.drawLine(marginLeft, yPosition, marginRight, yPosition, paint)
            
            // ===== INFORMACIÓN DEL PACIENTE =====
            yPosition += 25f
            
            paint.apply {
                color = colorNegro
                textSize = 11f
                typeface = Typeface.DEFAULT
                textAlign = Paint.Align.LEFT
                style = Paint.Style.FILL
            }
            
            // Datos del paciente en dos columnas
            val columnaIzq = marginLeft
            val columnaDer = marginLeft + 300f
            
            // Columna izquierda
            canvas.drawText("Paciente: ${paciente.nombre} ${paciente.apellido}", columnaIzq, yPosition, paint)
            yPosition += 15f
            canvas.drawText("Edad: ${paciente.edad} años", columnaIzq, yPosition, paint)
            yPosition += 15f
            canvas.drawText("Peso: ${consulta.peso ?: "N/A"} kg", columnaIzq, yPosition, paint)
            
            // Columna derecha
            yPosition -= 31f // Volver arriba para la columna derecha
            canvas.drawText("Fecha: ${consulta.fecha}", columnaDer, yPosition, paint) // Más espacio a la fecha
            yPosition += 15f
            canvas.drawText("Presión: ${consulta.presionArterial ?: "N/A"} mmHg", columnaDer, yPosition, paint)
            yPosition += 15f
            canvas.drawText("Altura: ${consulta.altura} m", columnaDer, yPosition, paint)
            
            // ===== MEDICAMENTOS =====
            yPosition += 35f // Más espacio antes de medicamentos
            
            paint.apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("MEDICAMENTOS:", marginLeft, yPosition, paint)
            
            yPosition += 20f
            paint.apply {
                textSize = 11f // Texto más grande para medicamentos
                typeface = Typeface.DEFAULT
            }
            
            // Calcular qué medicamentos mostrar en esta página
            val inicioMedicamentos = (paginaActual - 1) * medicamentosPorPagina
            val medicamentosEstaPagina = consulta.medicamentos.drop(inicioMedicamentos).take(medicamentosPorPagina)
            
            // Lista de medicamentos con formato específico
            medicamentosEstaPagina.forEachIndexed { index, medicamento ->
                val numeroMedicamento = inicioMedicamentos + index + 1
                val medicamentoText = "${numeroMedicamento}. ${medicamento.nombre} ${medicamento.dosis} Tableta diaria vía: ${medicamento.viaAdministracion ?: "Oral"} por ${medicamento.diasPrescripcion}"
                
                // Dividir texto largo en múltiples líneas si es necesario
                val maxWidth = marginRight - marginLeft - 20f
                val palabras = medicamentoText.split(" ")
                var lineaActual = ""
                var yLinea = yPosition
                
                palabras.forEach { palabra ->
                    val textoPrueba = if (lineaActual.isEmpty()) palabra else "$lineaActual $palabra"
                    paint.getTextBounds(textoPrueba, 0, textoPrueba.length, Rect())
                    
                    if (paint.measureText(textoPrueba) > maxWidth && lineaActual.isNotEmpty()) {
                        canvas.drawText(lineaActual, marginLeft + 15f, yLinea, paint)
                        yLinea += 14f // Más espacio entre líneas
                        lineaActual = palabra
                    } else {
                        lineaActual = textoPrueba
                    }
                }
                
                if (lineaActual.isNotEmpty()) {
                    canvas.drawText(lineaActual, marginLeft + 15f, yLinea, paint)
                    yPosition = yLinea + 18f // Más espacio entre medicamentos
                }
            }
            
            // ===== DIAGNÓSTICO =====
            yPosition += 15f // Más espacio antes del diagnóstico
            paint.apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("DIAGNÓSTICO:", marginLeft, yPosition, paint)
            
            yPosition += 15f
            paint.apply {
                textSize = 10f
                typeface = Typeface.DEFAULT
            }
            
            if (consulta.diagnosticoPrincipal.isNotEmpty()) {
                canvas.drawText(consulta.diagnosticoPrincipal, marginLeft + 15f, yPosition, paint)
            } else {
                canvas.drawText("Diagnóstico del paciente", marginLeft + 15f, yPosition, paint)
            }
            
            // ===== FIRMA DEL DOCTOR (AL MISMO NIVEL QUE DIAGNÓSTICO, A LA DERECHA) =====
            // Volver a la posición del diagnóstico para alinear la firma
            yPosition -= 25f // Volver al nivel del título "DIAGNÓSTICO:"
            
                         // Línea para firma
             paint.apply {
                 color = colorNegro
                 strokeWidth = 1f
                 style = Paint.Style.STROKE
             }
             canvas.drawLine(marginRight - 160f, yPosition, marginRight, yPosition, paint)
            
                         // Dibujar la firma del doctor si existe
             if (!doctor.firmaBase64.isNullOrEmpty()) {
                 try {
                     val firmaBitmap = decodeBase64ToBitmap(doctor.firmaBase64)
                     if (firmaBitmap != null) {
                         // Calcular dimensiones de la firma manteniendo la proporción
                         val maxFirmaWidth = 150f
                         val maxFirmaHeight = 80f
                         
                         // Calcular la proporción para mantener el aspecto original
                         val aspectRatio = firmaBitmap.width.toFloat() / firmaBitmap.height.toFloat()
                         
                         var firmaWidth = maxFirmaWidth
                         var firmaHeight = firmaWidth / aspectRatio
                         
                         // Si la altura es muy grande, ajustar por altura
                         if (firmaHeight > maxFirmaHeight) {
                             firmaHeight = maxFirmaHeight
                             firmaWidth = firmaHeight * aspectRatio
                         }
                         
                         // Si el ancho es muy grande, ajustar por ancho
                         if (firmaWidth > maxFirmaWidth) {
                             firmaWidth = maxFirmaWidth
                             firmaHeight = firmaWidth / aspectRatio
                         }
                         
                         val firmaX = marginRight - firmaWidth - 10f
                         val firmaY = yPosition - firmaHeight + 5f
                         
                         // Dibujar la firma con las dimensiones calculadas
                         val firmaRect = Rect(0, 0, firmaBitmap.width, firmaBitmap.height)
                         val destRect = RectF(firmaX, firmaY, firmaX + firmaWidth, firmaY + firmaHeight)
                         canvas.drawBitmap(firmaBitmap, firmaRect, destRect, paint)
                         
                         firmaBitmap.recycle()
                     }
                 } catch (e: Exception) {
                     // Si hay error al cargar la firma, continuar sin ella
                 }
             }
            
            yPosition += 10f
            paint.apply {
                color = colorNegro
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
                style = Paint.Style.FILL
            }
            canvas.drawText("DR. ${doctor.nombre} ${doctor.apellido}", marginRight, yPosition, paint)
            
            yPosition += 12f
            paint.apply {
                textSize = 9f
                typeface = Typeface.DEFAULT
            }
            canvas.drawText("${doctor.especialidad} - Céd. ${doctor.cedulaProfesional}", marginRight, yPosition, paint)
            
            // ===== FOOTER =====
            yPosition = 310f // Cerca del final de la página
            
            // Línea separadora
            paint.apply {
                color = colorNegro
                strokeWidth = 1f
                style = Paint.Style.STROKE
            }
            canvas.drawLine(marginLeft, yPosition, marginRight, yPosition, paint)
            
            yPosition += 15f
            
            // Información de contacto simplificada
            paint.apply {
                color = colorNegro
                textSize = 9f
                typeface = Typeface.DEFAULT
                textAlign = Paint.Align.LEFT
                style = Paint.Style.FILL
            }
            
            canvas.drawText("Tel: ${doctor.telefono ?: "N/A"}", marginLeft, yPosition, paint)
            
            paint.apply {
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("Email: ${doctor.correo ?: "N/A"}", marginRight, yPosition, paint)
            
            // Número de página si hay más de una
            if (totalPaginas > 1) {
                paint.apply {
                    color = colorGris
                    textSize = 8f
                    typeface = Typeface.DEFAULT
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("Página $paginaActual de $totalPaginas", 311.5f, 330f, paint)
            }
        }
        
        private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
            return try {
                val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                null
            }
        }
    }
} 