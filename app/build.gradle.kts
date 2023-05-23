plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementationAndroidX()
    implementationCoroutine()
    implementationHilt()
    implementationTest()
}