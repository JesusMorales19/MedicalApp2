# 🏥 MedicalApp - Sistema Médico Integral

## 📋 Descripción General

MedicalApp es una aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose** que proporciona un sistema integral de gestión médica. La aplicación permite a doctores y secretarias gestionar pacientes, consultas, cirugías, radiografías y generar recetas médicas con firma digital, además de proyectar información en TV mediante WebSocket.

## 🚀 Características Principales

### 👨‍⚕️ **Gestión de Doctores**
- Registro e inicio de sesión de doctores
- Perfil personalizable con foto y firma digital
- Gestión de información profesional (cédula, especialidad, etc.)

### 👩‍💼 **Gestión de Secretarias**
- Sistema de roles diferenciados
- Interfaz adaptada para tareas administrativas

### 👥 **Gestión de Pacientes**
- Registro completo de pacientes
- Historial médico detallado
- Información personal y médica

### 📝 **Consultas Médicas**
- Creación y gestión de consultas
- Diagnósticos y tratamientos
- Generación automática de recetas médicas

### 🏥 **Cirugías**
- Registro de procedimientos quirúrgicos
- Historial de cirugías por paciente
- Información detallada de intervenciones

### 📷 **Radiografías**
- Gestión de estudios radiológicos
- Almacenamiento de imágenes
- Historial de radiografías

### 📄 **Recetas Médicas**
- Generación automática de PDFs
- Firma digital del doctor
- Formato profesional y legal

### 📺 **Proyección en TV**
- Servidor WebSocket integrado
- Proyección en tiempo real
- Conexión automática a dispositivos externos

## 🛠️ Tecnologías y Librerías Utilizadas

### **Core Android & Kotlin**
```kotlin
// Versiones principales
compileSdk = 35
minSdk = 24
targetSdk = 35
kotlin = "2.0.21"
```

### **Jetpack Compose (UI Moderna)**
```kotlin
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.ui)
implementation(libs.androidx.ui.graphics)
implementation(libs.androidx.material3)
```
**Propósito**: Framework moderno de UI declarativa para Android
- **Material3**: Diseño Material Design 3
- **Compose BOM**: Gestión de versiones de Compose
- **UI Graphics**: Gráficos y animaciones

### **Firebase (Backend y Autenticación)**
```kotlin
implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
implementation("com.google.firebase:firebase-analytics")
implementation("com.google.firebase:firebase-firestore")
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-storage")
```
**Propósito**: Plataforma de backend completa
- **Firestore**: Base de datos NoSQL en tiempo real
- **Auth**: Autenticación de usuarios
- **Storage**: Almacenamiento de archivos (fotos, radiografías)
- **Analytics**: Análisis de uso de la aplicación

### **Coil (Carga de Imágenes)**
```kotlin
implementation("io.coil-kt:coil-compose:2.4.0")
```
**Propósito**: Carga eficiente de imágenes
- Carga asíncrona de imágenes
- Cache automático
- Optimización de memoria

### **WebSocket (Comunicación en Tiempo Real)**
```kotlin
implementation("org.java-websocket:Java-WebSocket:1.5.3")
```
**Propósito**: Comunicación bidireccional en tiempo real
- Proyección de datos en TV
- Sincronización en tiempo real
- Servidor WebSocket integrado

### **Gson (Serialización JSON)**
```kotlin
implementation("com.google.code.gson:gson:2.10.1")
```
**Propósito**: Conversión de objetos a JSON y viceversa
- Serialización de datos para WebSocket
- Parsing de respuestas de API

### **Lifecycle Components**
```kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
```
**Propósito**: Gestión del ciclo de vida de la aplicación
- **ViewModel**: Gestión de estado de UI
- **Runtime Compose**: Integración con Compose

### **LocalBroadcastManager**
```kotlin
implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
```
**Propósito**: Comunicación entre componentes de la aplicación
- Notificaciones internas
- Actualización de estado de servicios

## 🏗️ Arquitectura del Proyecto

### **Estructura de Paquetes**
```
com.tuempresa.medicalapp/
├── core/
│   ├── constants/          # Constantes de la aplicación
│   ├── extensions/         # Extensiones de Kotlin
│   ├── network/           # Servicios de red y WebSocket
│   └── utils/             # Utilidades generales
├── data/
│   ├── models/            # Modelos de datos
│   └── repositories/      # Repositorios de datos
├── domain/
│   ├── entities/          # Entidades de dominio
│   └── usecases/          # Casos de uso
├── presentation/
│   ├── components/        # Componentes reutilizables
│   ├── navigation/        # Navegación
│   ├── screens/           # Pantallas de la aplicación
│   ├── theme/             # Temas y estilos
│   └── viewmodels/        # ViewModels
└── MainActivity.kt        # Actividad principal
```

## 📱 Funcionalidades Detalladas

### **1. Sistema de Autenticación**
- **Firebase Auth**: Autenticación segura
- **Roles**: Doctor y Secretaria
- **Persistencia**: Sesión mantenida
- **Validación**: Campos obligatorios y formato

### **2. Gestión de Pacientes**
```kotlin
data class Paciente(
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val edad: Int = 0,
    val genero: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)
```

### **3. Sistema de Firma Digital**
```kotlin
// Componente de firma interactiva
@Composable
fun SignatureDialog(
    onDismiss: () -> Unit,
    onSignatureSaved: (String) -> Unit
)
```

**Características**:
- **Canvas interactivo**: Dibujo con el dedo
- **Grosor configurable**: 3px, 5px, 8px
- **Captura completa**: Sin recorte de la firma
- **Compresión inteligente**: Optimización automática
- **Formato PNG**: Fondo transparente
- **Base64**: Almacenamiento en base de datos

### **4. Generación de Recetas Médicas**
```kotlin
class RecetaGenerator {
    companion object {
        fun generarRecetaPDF(
            context: Context,
            consulta: Consulta,
            doctor: Doctor,
            paciente: Paciente
        ): File
    }
}
```

**Características**:
- **PDF profesional**: Formato médico estándar
- **Firma integrada**: Firma digital del doctor
- **Información completa**: Datos del paciente y medicamentos
- **Múltiples páginas**: Soporte para recetas largas
- **Dimensiones**: 22x12 cm (formato receta)

### **5. Proyección en TV (WebSocket)**
```kotlin
class ProyeccionService : Service() {
    private var webSocketServer: DoctorWebSocketServer? = null
}
```

**Características**:
- **Servidor WebSocket**: Puerto dinámico (9095+)
- **Servicio en segundo plano**: Notificación persistente
- **Conexión automática**: Búsqueda de puertos disponibles
- **Broadcast local**: Comunicación con la UI
- **Múltiples clientes**: Soporte para varias TVs

### **6. Gestión de Imágenes**
- **Coil**: Carga eficiente de imágenes
- **Base64**: Almacenamiento de fotos y firmas
- **Compresión**: Optimización automática
- **Cache**: Almacenamiento local

## 🔧 Configuración del Proyecto

### **Requisitos Previos**
- Android Studio Hedgehog o superior
- JDK 11
- Android SDK 35
- Dispositivo Android API 24+

### **Configuración de Firebase**
1. Crear proyecto en Firebase Console
2. Descargar `google-services.json`
3. Colocar en `app/` directory
4. Habilitar servicios: Auth, Firestore, Storage

### **Configuración de WebSocket**
1. Verificar puertos disponibles (9095+)
2. Configurar firewall si es necesario
3. Probar conexión con cliente WebSocket

## 📊 Modelos de Datos

### **Doctor**
```kotlin
data class Doctor(
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val telefono: String = "",
    val especialidad: String = "",
    val cedulaProfesional: String = "",
    val firmaBase64: String = "", // Firma digital
    val fotoBase64: String = "",
    val rol: String = "doctor"
)
```

### **Consulta**
```kotlin
data class Consulta(
    val id: String = "",
    val pacienteId: String = "",
    val doctorId: String = "",
    val fecha: String = "",
    val peso: Float? = null,
    val altura: Float = 0f,
    val presionArterial: String? = null,
    val diagnosticoPrincipal: String = "",
    val medicamentos: List<Medicamento> = emptyList()
)
```

### **Medicamento**
```kotlin
data class Medicamento(
    val nombre: String = "",
    val dosis: String = "",
    val viaAdministracion: String = "Oral",
    val diasPrescripcion: String = ""
)
```

## 🎨 UI/UX Design

### **Material Design 3**
- **Colores**: Paleta médica profesional
- **Tipografía**: Jerarquía clara
- **Componentes**: Cards, Buttons, TextFields
- **Navegación**: Bottom Navigation

### **Responsive Design**
- **Adaptable**: Diferentes tamaños de pantalla
- **Orientación**: Portrait y Landscape
- **Accesibilidad**: Contraste y tamaños adecuados

## 🔒 Seguridad

### **Autenticación**
- Firebase Auth con email/password
- Validación de roles
- Sesiones seguras

### **Datos**
- Firestore con reglas de seguridad
- Encriptación de datos sensibles
- Validación de entrada

### **Permisos**
- Almacenamiento para imágenes
- Internet para WebSocket
- Notificaciones para servicios

## 📱 Compatibilidad

### **Versiones Android**
- **Mínimo**: API 24 (Android 7.0)
- **Objetivo**: API 35 (Android 15)
- **Compilación**: API 35

### **Dispositivos**
- **Teléfonos**: Optimizado para móviles
- **Tablets**: Soporte básico
- **TV**: Proyección WebSocket

## 🎯 Conclusión

MedicalApp es una solución completa y moderna para la gestión médica, combinando las mejores prácticas de desarrollo Android con tecnologías de vanguardia. La aplicación proporciona una experiencia de usuario excepcional mientras mantiene la seguridad y confiabilidad necesarias en el entorno médico.

**Tecnologías clave**: Kotlin, Jetpack Compose, Firebase, WebSocket, PDF Generation, Digital Signatures

**Funcionalidades destacadas**: Gestión integral de pacientes, recetas médicas con firma digital, proyección en TV en tiempo real, sistema de roles diferenciados. 