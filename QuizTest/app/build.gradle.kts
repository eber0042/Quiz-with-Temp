plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // KSP Gradle Plugin
    id("com.google.devtools.ksp")

    // Hilt Gradle Plugin
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    // Kotlinx Serialization Plugin
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    namespace = "com.temi.temiSDK"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.temi.temiSDK"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(project(":OpenCV"))
    implementation(libs.androidx.foundation.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Temi SDK
    implementation("com.robotemi:sdk:1.133.0")

    //GIF <-Stuff that is used for getting gifs working
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-gif:2.6.0")

    // Dependencies for Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Android Activity Library
    implementation("androidx.activity:activity-ktx:1.9.0")

    // JSON serialization library dependency
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0") // Replace <latest-ver

    // constraint layout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // LiteRT dependencies for Google Play services
    implementation("com.google.android.gms:play-services-tflite-java:16.1.0")

    // Optional: include LiteRT Support Library
    implementation("com.google.android.gms:play-services-tflite-support:16.1.0")

    // Stuff to hopefully get OpnCV Working
    implementation ("androidx.camera:camera-core:1.0.0") // Camera2 library dependency
    implementation ("androidx.camera:camera-camera2:1.0.0")

    // Stuff to get composable to overlay on the camera view
    implementation ("androidx.compose.ui:ui:1.4.0") // Use the latest stable version
    implementation ("androidx.compose.material:material:1.4.0") // Material Design components
    implementation ("androidx.compose.ui:ui-tooling-preview:1.4.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0") // For lifecycle management
}

kapt {
    correctErrorTypes = true
}