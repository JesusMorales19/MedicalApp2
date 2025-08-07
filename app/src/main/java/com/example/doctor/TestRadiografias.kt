package com.example.doctor

/**
 * Archivo de prueba para verificar la funcionalidad de radiografías
 * 
 * Ejemplo de JSON que debe enviarse desde la app móvil:
 * 
 * {
 *   "tipo": "radiografias",
 *   "tipoRadiografia": "Tórax",
 *   "paciente": {
 *     "nombre": "Juan",
 *     "apellido": "Pérez"
 *   },
 *   "radiografias": [
 *     {
 *       "fecha": "15/12/2024",
 *       "imagenBase64": "base64_string_de_la_radiografia",
 *       "observaciones": "Observaciones del médico sobre la radiografía de tórax"
 *     },
 *     {
 *       "fecha": "20/12/2024", 
 *       "imagenBase64": "base64_string_de_la_radiografia_2",
 *       "observaciones": "Nuevas observaciones sobre la evolución"
 *     }
 *   ]
 * }
 * 
 * Flujo de funcionamiento:
 * 1. La app móvil envía este JSON vía WebSocket a la TV
 * 2. El TVDataViewModel procesa el mensaje y detecta tipo "radiografias"
 * 3. Se crea el objeto RadiografiaData y se activa mostrarRadiografias = true
 * 4. El ExpedienteScreen detecta el cambio y muestra el modal RadiografiasModal
 * 5. El modal muestra las radiografías en carrusel con zoom y navegación
 * 6. Al cerrar el modal, se vuelve a ExpedienteScreen normal
 * 
 * Características implementadas:
 * ✅ Recepción de JSON con tipo "radiografias"
 * ✅ Modal overlay encima de ExpedienteScreen
 * ✅ Carrusel de radiografías múltiples
 * ✅ Zoom y pan en las imágenes
 * ✅ Navegación con flechas prev/next
 * ✅ Indicador de posición (1 de 3, 2 de 3, etc.)
 * ✅ Controles de zoom (zoom in, zoom out, reset)
 * ✅ Información de fecha y observaciones
 * ✅ Iconos según tipo de radiografía
 * ✅ Diseño médico profesional
 * ✅ Cerrar con ESC o botón X
 * ✅ Fondo semi-transparente
 * ✅ Transiciones suaves
 */ 