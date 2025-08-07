plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.example.doctor"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.doctor"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core y Compose base
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Android TV (por si usas diseño tipo TV)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)

    //  Navegación entre pantallas
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //  Material Icons extendidos
    implementation("androidx.compose.material:material-icons-extended")

    //  Coil para carga de imágenes
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Material3 compose
    implementation("androidx.compose.material3:material3:1.3.2")


    // Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    
    // SLF4J para Android - Para que los logs de WebSocket se muestren
    implementation("org.slf4j:slf4j-android:1.7.36")
}