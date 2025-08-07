package com.tuempresa.medicalapp.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

object ImageUtils {
    
    /**
     * Convierte una cadena Base64 a ImageBitmap
     */
    fun base64ToImageBitmap(base64Str: String): ImageBitmap? {
        return try {
            if (base64Str.isBlank()) return null
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Convierte Uri a Bitmap
     */
    fun uriToBitmap(context: android.content.Context, uri: android.net.Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Comprime y convierte Uri a Base64
     */
    fun compressAndConvertToBase64(context: android.content.Context, uri: android.net.Uri): String {
        return try {
            val bitmap = uriToBitmap(context, uri)
            bitmap?.let { compressAndConvertToBase64(it) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Comprime y convierte un bitmap a Base64
     */
    fun compressAndConvertToBase64(bitmap: Bitmap): String {
        // Redimensionar la imagen si es muy grande para evitar SQLiteBlobTooBigException
        val maxSize = 800
        val resizedBitmap = if (bitmap.width > maxSize || bitmap.height > maxSize) {
            val scale = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
        
        // Comprimir con calidad adaptativa para reducir el tamaño de archivo
        var quality = 100
        var outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        var byteArray = outputStream.toByteArray()
        
        // Si el archivo es muy grande (>1MB), reducir la calidad gradualmente
        while (byteArray.size > 1024 * 1024 && quality > 10) { // 1MB = 1024 * 1024 bytes
            quality -= 10
            outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            byteArray = outputStream.toByteArray()
        }
        
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    
    /**
     * Decodifica Base64 a Bitmap
     */
    fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            if (base64Str.isBlank()) return null
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Comprime una imagen Base64 a un tamaño máximo
     */
    fun compressBase64Image(base64Str: String, maxSizeKB: Int = 300): String {
        return try {
            if (base64Str.isBlank()) return ""
            
            // Decodificar la imagen original
            val originalBitmap = decodeBase64ToBitmap(base64Str) ?: return ""
            
            // Redimensionar si es muy grande
            val maxDimension = 600
            val resizedBitmap = if (originalBitmap.width > maxDimension || originalBitmap.height > maxDimension) {
                val scale = maxDimension.toFloat() / maxOf(originalBitmap.width, originalBitmap.height)
                val newWidth = (originalBitmap.width * scale).toInt()
                val newHeight = (originalBitmap.height * scale).toInt()
                Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            } else {
                originalBitmap
            }
            
            // Comprimir con calidad adaptativa
            var quality = 80
            var outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            var byteArray = outputStream.toByteArray()
            
            // Reducir calidad hasta alcanzar el tamaño máximo
            while (byteArray.size > maxSizeKB * 1024 && quality > 10) {
                quality -= 10
                outputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                byteArray = outputStream.toByteArray()
            }
            
            // Limpiar memoria
            if (resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }
            
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            // Si falla la compresión, devolver la original
            base64Str
        }
    }
} 