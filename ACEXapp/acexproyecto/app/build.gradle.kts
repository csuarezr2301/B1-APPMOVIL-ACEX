plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.acexproyecto"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.acexproyecto"
        minSdk = 34
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
    // Configuración de compatibilidad con Java 8
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Habilitar ViewBinding
    buildFeatures {
        viewBinding = true
        compose = true // Ya lo tienes habilitado, solo recordamos que Compose sigue habilitado
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
        excludes += "META-INF/DEPENDENCIES"
        excludes += "META-INF/INDEX.LIST"
        excludes += "META-INF/AL2.0"
        excludes += "META-INF/LGPL2.1"
        excludes += "META-INF/LICENSE.md"
        excludes += "META-INF/NOTICE.md"
        excludes += "META-INF/io.netty.versions.properties"
        excludes += "META-INF/license/LICENSE.aix-netbsd.txt"
        excludes += "google/firestore/v1/query.proto"
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
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation(libs.androidx.runtime.livedata)
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.transport.api)
    implementation(libs.protolite.well.known.types)
    implementation(libs.play.services.maps)

    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.maps.android:maps-compose:2.0.0")

    implementation("io.coil-kt:coil-compose:2.2.0")

    // Dependencias de CameraX
    implementation ("androidx.camera:camera-core:1.1.0-beta01")
    implementation ("androidx.camera:camera-camera2:1.1.0-beta01")
    implementation ("androidx.camera:camera-lifecycle:1.1.0-beta01")
    implementation ("androidx.camera:camera-video:1.1.0-beta01")
    implementation ("androidx.camera:camera-view:1.1.0-beta01")
    implementation ("androidx.camera:camera-extensions:1.1.0-beta01")

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // MSAL
    implementation("com.microsoft.identity.client:msal:1.4.0")
    implementation("com.microsoft.graph:microsoft-graph:3.0.0") {
        exclude(group = "javax.activation", module = "activation")
    }
}