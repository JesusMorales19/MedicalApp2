package com.example.doctor.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.doctor.Model.RadiografiaData
import com.example.doctor.Model.Radiografia
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.geometry.Offset

@Composable
fun RadiografiasModal(
    radiografiaData: RadiografiaData,
    onClose: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    // Ordenar radiografías por fecha más reciente primero
    val radiografias = radiografiaData.radiografias.sortedByDescending { it.fecha }
    
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
        ) {
            // Contenido principal
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header mejorado
                    RadiografiasHeader(
                        tipoRadiografia = radiografiaData.tipoRadiografia,
                        paciente = radiografiaData.paciente,
                        onClose = onClose
                    )
                    
                    // Contenido principal con layout mejorado
                    if (radiografias.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Panel izquierdo: Imagen de la radiografía
                                Box(
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxHeight()
                                        .padding(16.dp)
                                ) {
                                    RadiografiaImageView(
                                        radiografia = radiografias[currentIndex],
                                        scale = scale,
                                        offset = offset,
                                        onScaleChange = { scale = it },
                                        onOffsetChange = { offset = it }
                                    )
                                    
                                    // Controles de zoom mejorados (más visibles)
                                    ZoomControlsImproved(
                                        scale = scale,
                                        onZoomIn = { scale = (scale * 1.2f).coerceIn(0.5f..3f) },
                                        onZoomOut = { scale = (scale / 1.2f).coerceIn(0.5f..3f) },
                                        onReset = { 
                                            scale = 1f
                                            offset = Offset.Zero
                                        },
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                                
                                // Panel derecho: Información y controles
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color(0xFFF8F9FA))
                                        .padding(20.dp)
                                ) {
                                    // Contenido scrolleable
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                    // Información de la radiografía actual
                                    RadiografiaInfoPanel(
                                        radiografia = radiografias[currentIndex],
                                        currentIndex = currentIndex,
                                        totalRadiografias = radiografias.size
                                    )
                                    
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    // Controles de navegación mejorados
                                    RadiografiasNavigationImproved(
                                        currentIndex = currentIndex,
                                        totalRadiografias = radiografias.size,
                                        onPrevious = { 
                                            if (currentIndex > 0) {
                                                currentIndex--
                                                scale = 1f
                                                offset = Offset.Zero
                                            }
                                        },
                                        onNext = { 
                                            if (currentIndex < radiografias.size - 1) {
                                                currentIndex++
                                                scale = 1f
                                                offset = Offset.Zero
                                            }
                                        }
                                    )
                                    
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    // Lista de todas las radiografías
                                    RadiografiasList(
                                        radiografias = radiografias,
                                        currentIndex = currentIndex,
                                        onRadiografiaClick = { index ->
                                            currentIndex = index
                                            scale = 1f
                                            offset = Offset.Zero
                                        }
                                    )
                                    }
                                }
                            }
                        }
                    } else {
                        // Sin radiografías
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.ImageNotSupported,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No hay radiografías disponibles",
                                    fontSize = 18.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RadiografiasHeader(
    tipoRadiografia: String,
    paciente: com.example.doctor.Model.PacienteRadiografia,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono según tipo de radiografía
            Icon(
                imageVector = when (tipoRadiografia.lowercase()) {
                    "tórax" -> Icons.Default.Person
                    "columna" -> Icons.Default.Straighten
                    "extremidades" -> Icons.Default.Accessibility
                    else -> Icons.Default.Image
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Título
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Radiografías de $tipoRadiografia",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${paciente.nombre} ${paciente.apellido}",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            
            // Botón cerrar mejorado
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onClose() },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RadiografiaImageView(
    radiografia: Radiografia,
    scale: Float,
    offset: Offset,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Offset) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        onScaleChange((scale * zoom).coerceIn(0.5f..3f))
                        onOffsetChange(offset + pan)
                    }
                }
        ) {
            if (radiografia.imagenBase64.isNotEmpty()) {
                val bitmap = try {
                    val imageBytes = Base64.decode(radiografia.imagenBase64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                } catch (e: Exception) {
                    null
                }
                
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Radiografía",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )
                } else {
                    // Error al cargar imagen
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.BrokenImage,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Error al cargar la imagen",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomControlsImproved(
    scale: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onZoomIn() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ZoomIn,
                    contentDescription = "Zoom In",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onZoomOut() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ZoomOut,
                    contentDescription = "Zoom Out",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onReset() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun RadiografiaInfoPanel(
    radiografia: Radiografia,
    currentIndex: Int,
    totalRadiografias: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Indicador de posición
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Radiografía ${currentIndex + 1} de $totalRadiografias",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Fecha
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fecha:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = radiografia.fecha,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(start = 28.dp, top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Observaciones
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.NoteAlt,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Observaciones:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = if (radiografia.observaciones.isNotEmpty()) radiografia.observaciones else "Sin observaciones",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 28.dp, top = 4.dp, bottom = 8.dp),
                lineHeight = 20.sp,
                textAlign = TextAlign.Justify
            )
            
            // Indicador de scroll si el texto es largo
            if (radiografia.observaciones.length > 200) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll disponible",
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Desliza para ver más",
                        fontSize = 12.sp,
                        color = Color(0xFF1565C0),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun RadiografiasNavigationImproved(
    currentIndex: Int,
    totalRadiografias: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón anterior
            Card(
                modifier = Modifier
                    .size(56.dp)
                    .clickable(enabled = currentIndex > 0) { onPrevious() },
                colors = CardDefaults.cardColors(
                    containerColor = if (currentIndex > 0) Color(0xFF1565C0) else Color(0xFFE0E0E0)
                ),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Anterior",
                        tint = if (currentIndex > 0) Color.White else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // Indicador de posición
            Text(
                text = "${currentIndex + 1} / $totalRadiografias",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1565C0)
            )
            
            // Botón siguiente
            Card(
                modifier = Modifier
                    .size(56.dp)
                    .clickable(enabled = currentIndex < totalRadiografias - 1) { onNext() },
                colors = CardDefaults.cardColors(
                    containerColor = if (currentIndex < totalRadiografias - 1) Color(0xFF1565C0) else Color(0xFFE0E0E0)
                ),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Siguiente",
                        tint = if (currentIndex < totalRadiografias - 1) Color.White else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RadiografiasList(
    radiografias: List<Radiografia>,
    currentIndex: Int,
    onRadiografiaClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Todas las radiografías",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            radiografias.forEachIndexed { index, radiografia ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onRadiografiaClick(index) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (index == currentIndex) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = if (index == currentIndex) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1565C0)) else null
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            tint = if (index == currentIndex) Color(0xFF1565C0) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Radiografía ${index + 1}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (index == currentIndex) Color(0xFF1565C0) else Color.Black
                            )
                            Text(
                                text = radiografia.fecha,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        
                        if (index == currentIndex) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF1565C0),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
} 