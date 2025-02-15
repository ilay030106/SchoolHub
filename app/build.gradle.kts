import java.util.regex.Pattern.compile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.schoolhub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.schoolhub"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase dependencies
    implementation(libs.firebase.storage) // Firebase Storage
    implementation(libs.firebase.auth)   // Firebase Authentication
    implementation(libs.firebase.database) // Realtime Database
    implementation(libs.firebase.firestore) // Firestore
    implementation(libs.firebase.ui.storage) // Firebase UI for Storage
    implementation(libs.firebase.ui.database) // Firebase UI for Realtime Database
    implementation(libs.firebase.analytics) // Firebase Analytics
    implementation(platform(libs.firebase.bom)) // Firebase BOM
    implementation(libs.firebase.ui.auth) // Firebase UI for Authentication

    // AndroidX dependencies
    implementation(libs.appcompat)
    implementation(libs.material) // Material Design
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation (libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.gson)
    implementation(libs.library)

    // Miscellaneous
    implementation(libs.annotation)
    implementation(libs.xcom.google.android.material.material)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "com.android.support") {
                useVersion("28.0.0")
            }
        }
    }

}
