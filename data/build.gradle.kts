plugins {
    id("com.android.library")
    kotlin("android")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = Apps.ApplicationId + ".data"
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
    implementation(project(":domain"))
    implementation(project(":core:common"))

    implementationAndroidX()
    implementationCoroutine()
    implementationHilt()
    implementationLocal()
    implementationTest()

    implementationFirebase()
    implementations(
        Libraries.Timber,
        Libraries.Firebase.Auth,
        Libraries.Firebase.GmsAuth,
        Libraries.Firebase.Function,
        Libraries.Firebase.DataBase
    )
}