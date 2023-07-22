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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE",
                "META-INF/LICENSE.md",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/LICENSE-notice.md",
                "META-INF/DEPENDENCIES",
                "META-INF/AL2.0",
            )
        )
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:test"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":local"))
    implementation(project(":remote"))

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
        Libraries.Kakao,
        Libraries.Coil
    )
}

apply(plugin = "com.google.gms.google-services")