// Fichier : app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    // Application du plugin KSP (la version est dans le fichier racine)
    id("com.google.devtools.ksp")
}

// Déclaration de la variable de Room en KOTLIN DSL
val room_version = "2.7.0-alpha03"

android {
    namespace = "com.samaali.codememo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.samaali.codememo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
} // Fin du bloc android {}

dependencies {
    // BOM (Bill of Materials) pour Compose
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))

    // Dépendances Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    // Dépendances Firebase spécifiques
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Coil pour les photos
    implementation("io.coil-kt:coil-compose:2.7.0")

    // AJOUTS POUR ROOM, utilisation de la variable val
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}
