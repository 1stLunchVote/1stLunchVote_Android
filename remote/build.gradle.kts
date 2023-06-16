plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = Apps.ApplicationId + ".remote"
    compileSdk = Apps.CompileSdk

    defaultConfig {
        minSdk = Apps.MinSdk

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))

    implementationAndroidX()
    implementationHilt()
    implementationFirebase()
    implementations(
        Libraries.Timber,
        Libraries.Firebase.Auth,
        Libraries.Firebase.GmsAuth,
        Libraries.Firebase.Function,
        Libraries.Firebase.DataBase
    )
}