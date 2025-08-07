package com.tuempresa.medicalapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun RecetaDialog(
    onDismiss: () -> Unit,
    recetaFile: File,
    numeroPaciente: String? = null
) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Receta Generada",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF183A6D)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "La receta médica ha sido generada exitosamente.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    "Archivo: ${recetaFile.name}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    "Ubicación: ${recetaFile.absolutePath}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    "Puedes compartir la receta con el paciente o imprimirla usando los botones de abajo.",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón para compartir con paciente por WhatsApp
                Button(
                    onClick = {
                        try {
                            // Si tenemos el número del paciente, abrir WhatsApp con ese número
                            if (!numeroPaciente.isNullOrBlank()) {
                                val whatsappNumber = numeroPaciente.replace(" ", "").replace("-", "").replace("+", "")
                                val whatsappUri = Uri.parse("https://wa.me/$whatsappNumber")
                                
                                val whatsappIntent = Intent(Intent.ACTION_VIEW, whatsappUri).apply {
                                    putExtra("text", "Receta médica del paciente")
                                }
                                
                                // Verificar si WhatsApp está disponible
                                if (whatsappIntent.resolveActivity(context.packageManager) != null) {
                                    // Copiar el PDF a Downloads para facilitar el acceso
                                    copiarPDFaDownloads(context, recetaFile)
                                    
                                    // Abrir WhatsApp con el número del paciente
                                    context.startActivity(whatsappIntent)
                                    
                                    // Mostrar mensaje informativo
                                    android.widget.Toast.makeText(
                                        context,
                                        "WhatsApp abierto. Busca '${recetaFile.name}' en tus archivos para adjuntar.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    // Fallback: compartir PDF normalmente
                                    compartirPDFNormal(context, recetaFile)
                                }
                            } else {
                                // Si no hay número, compartir PDF normalmente
                                compartirPDFNormal(context, recetaFile)
                            }
                        } catch (e: Exception) {
                            // Manejar error
                            compartirPDFNormal(context, recetaFile)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // Color de WhatsApp
                ) {
                    Icon(Icons.Default.Whatsapp, contentDescription = "Compartir con paciente", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compartir con paciente", color = Color.White, fontSize = 12.sp)
                }
                
                // Botón para imprimir/ver
                Button(
                    onClick = {
                        try {
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                recetaFile
                            )
                            
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Manejar error
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Imprimir/Ver", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Imprimir/Ver", color = Color.White, fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF183A6D))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cerrar", color = Color(0xFF183A6D))
            }
        }
    )
}

// Función auxiliar para compartir PDF normalmente
private fun compartirPDFNormal(context: android.content.Context, recetaFile: File) {
    try {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            recetaFile
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Receta Médica")
            putExtra(Intent.EXTRA_TEXT, "Receta médica del paciente")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartir receta"))
    } catch (e: Exception) {
        // Manejar error
    }
}

// Función para copiar el PDF a Downloads
private fun copiarPDFaDownloads(context: android.content.Context, recetaFile: File) {
    try {
        val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
        val destinationFile = File(downloadsDir, recetaFile.name)
        
        // Copiar el archivo
        recetaFile.copyTo(destinationFile, overwrite = true)
        
        // Notificar al sistema de archivos que hay un nuevo archivo
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = android.net.Uri.fromFile(destinationFile)
        context.sendBroadcast(mediaScanIntent)
        
    } catch (e: Exception) {
        // Si no se puede copiar a Downloads, intentar copiar a la carpeta de la app
        try {
            val appDir = context.getExternalFilesDir(null)
            val destinationFile = File(appDir, recetaFile.name)
            recetaFile.copyTo(destinationFile, overwrite = true)
        } catch (e2: Exception) {
            // Si todo falla, no hacer nada
        }
    }
} 