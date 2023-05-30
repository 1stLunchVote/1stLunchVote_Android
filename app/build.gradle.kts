plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = Apps.ApplicationId
    compileSdk = Apps.CompileSdk

    defaultConfig {
        applicationId = Apps.ApplicationId
        minSdk = Apps.MinSdk
        targetSdk = Apps.CompileSdk
        versionCode = Apps.VersionCode
        versionName = Apps.VersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        dataBinding = true
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
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
    implementation(project(":core:ui"))
    implementation(project(":domain"))
    implementation(project(":data"))

    implementationAndroidX()
    implementationCompose()
    implementationCoroutine()
    implementationHilt()
    implementationTest()
    implementationFirebase()

    implementations(
        Libraries.Timber,
        Libraries.Firebase.Auth,
        Libraries.Firebase.GmsAuth,
        Libraries.Firebase.Function,
        Libraries.Kakao
    )
}

apply(plugin = "com.google.gms.google-services")