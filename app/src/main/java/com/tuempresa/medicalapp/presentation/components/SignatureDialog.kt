package com.tuempresa.medicalapp.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.ByteArrayOutputStream

@Composable
fun SignatureDialog(
    onDismiss: () -> Unit,
    onSignatureSaved: (String) -> Unit
) {
    var paths by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var strokeWidth by remember { mutableStateOf(3f) }
    
    val blue = Color(0xFF183A6D)
    val lightGray = Color(0xFFF5F7FA)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Firma Digital",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = blue,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(lightGray, RoundedCornerShape(8.dp))
                        .border(2.dp, blue, RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = listOf(offset)
                                },
                                onDragEnd = {
                                    if (currentPath.isNotEmpty()) {
                                        paths = paths + listOf(currentPath)
                                        currentPath = emptyList()
                                    }
                                }
                            ) { change, _ ->
                                currentPath = currentPath + change.position
                            }
                        }
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                                                 // Dibujar líneas guardadas
                         paths.forEach { path ->
                             if (path.size >= 2) {
                                 for (i in 0 until path.size - 1) {
                                     drawLine(
                                         color = Color.Black,
                                         start = path[i],
                                         end = path[i + 1],
                                         strokeWidth = strokeWidth
                                     )
                                 }
                             }
                         }
                         
                         // Dibujar línea actual
                         if (currentPath.size >= 2) {
                             for (i in 0 until currentPath.size - 1) {
                                 drawLine(
                                     color = Color.Black,
                                     start = currentPath[i],
                                     end = currentPath[i + 1],
                                     strokeWidth = strokeWidth
                                 )
                             }
                         }
                    }
                    
                    if (paths.isEmpty() && currentPath.isEmpty()) {
                        Text(
                            text = "Firma aquí con tu dedo",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            paths = emptyList()
                            currentPath = emptyList()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Limpiar")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            strokeWidth = if (strokeWidth == 3f) 5f else if (strokeWidth == 5f) 8f else 3f
                        }
                    ) {
                        Text("Grosor: ${strokeWidth.toInt()}px")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (paths.isNotEmpty()) {
                                val signatureBitmap = createSignatureBitmap(paths, 0, 0) // Dimensiones se calculan automáticamente
                                val base64Signature = bitmapToBase64(signatureBitmap)
                                onSignatureSaved(base64Signature)
                                onDismiss()
                            }
                        },
                        enabled = paths.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = blue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar Firma")
                    }
                }
            }
        }
    }
}

private fun createSignatureBitmap(paths: List<List<Offset>>, width: Int, height: Int): Bitmap {
    // Calcular los límites reales de la firma
    var minX = Float.MAX_VALUE
    var minY = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var maxY = Float.MIN_VALUE
    
    paths.forEach { path ->
        path.forEach { point ->
            minX = minOf(minX, point.x)
            minY = minOf(minY, point.y)
            maxX = maxOf(maxX, point.x)
            maxY = maxOf(maxY, point.y)
        }
    }
    
    // Agregar margen para que no se pegue a los bordes
    val margin = 40f
    val signatureWidth = maxX - minX + margin * 2
    val signatureHeight = maxY - minY + margin * 2
    
    // Crear bitmap con el tamaño exacto de la firma (sin recortar)
    val bitmapWidth = signatureWidth.toInt()
    val bitmapHeight = signatureHeight.toInt()
    
    // Asegurar un tamaño mínimo
    val finalWidth = maxOf(bitmapWidth, 100)
    val finalHeight = maxOf(bitmapHeight, 50)
    
    val bitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    
    canvas.drawColor(android.graphics.Color.TRANSPARENT)
    
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 3f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    
    // Dibujar la firma en su posición original (solo con el margen)
    paths.forEach { path ->
        if (path.size >= 2) {
            for (i in 0 until path.size - 1) {
                val startX = path[i].x - minX + margin
                val startY = path[i].y - minY + margin
                val endX = path[i + 1].x - minX + margin
                val endY = path[i + 1].y - minY + margin
                
                canvas.drawLine(startX, startY, endX, endY, paint)
            }
        }
    }
    
    return bitmap
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    
    // Si la firma es muy grande, comprimirla pero mantener buena calidad
    val maxSize = 1024 * 1024 // 1MB máximo
    var quality = 100
    var byteArray: ByteArray
    
    do {
        outputStream.reset()
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
        byteArray = outputStream.toByteArray()
        quality -= 10
    } while (byteArray.size > maxSize && quality > 20)
    
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
} 