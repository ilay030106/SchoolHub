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
    implementation(libs.firebase.storage) // Firebase Storage
    implementation(libs.firebase.auth)   // Firebase Authentication
    implementation(libs.firebase.database)// Realtime Database
    implementation(libs.firebase.database)// Realtime Database
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ui.storage)
    implementation(libs.firebase.ui.database)
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ui.auth)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.annotation)
    implementation(libs.xcom.google.android.material.material)
    implementation(libs.blurview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}