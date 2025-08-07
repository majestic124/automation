plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" // для Room
    alias(libs.plugins.navigation.safeargs)
//    alias(libs.plugins.google.services)
}

android {
    namespace = "com.automation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.automation.pay"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    //Naming of output build file
    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName =
                    "Automation - ${variant.baseName} - ${variant.versionName} - ${variant.versionCode}.apk"
                println("OutputFileName: $outputFileName")
                output.outputFileName = outputFileName
            }
    }

    buildTypes {
        release {
            buildConfigField("String", "API_KEY", "\"In7HoFd0I24Rr9APQI1GrwkoQ4xk3tjs\"")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            buildConfigField("String", "API_KEY", "\"In7HoFd0I24Rr9APQI1GrwkoQ4xk3tjs\"")
            isMinifyEnabled = true
            isShrinkResources = true
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
        kotlinCompilerExtensionVersion = "1.5.10"
    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraint.layout)
    implementation(libs.firebase.config)

    // Testing
    testImplementation(libs.junit.core)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)

    // UI
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // Compose Vm
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)


    // Compose
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)

    // Navigation
    implementation(libs.cicerone)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.compose)

    // Network
    implementation(libs.okhttp.logging)
    implementation(libs.okhttp)

    // DI
    implementation(libs.koin)
    implementation(libs.koin.compose)

    // Coroutines
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)

    // Room
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    //LOG
    implementation(libs.timber)

//    coreLibraryDesugaring(libs.desugar_jdk_libs)

}