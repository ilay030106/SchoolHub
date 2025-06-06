// build.gradle.kts (Kotlin DSL)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    id("io.freefair.lombok") version "8.4" // ✅ תוסף לומבוק
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ui.storage)
    implementation(libs.firebase.ui.database)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.ui.auth)

    // AndroidX dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.gson)
    implementation(libs.library)

    // Miscellaneous
    implementation(libs.annotation)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // mXparser
    implementation(libs.mathparser.org.mxparser)

    // ✅ Lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

// ✅ שים את זה מחוץ ל־dependencies
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.android.support") {
            useVersion("28.0.0")
        }
    }
}
