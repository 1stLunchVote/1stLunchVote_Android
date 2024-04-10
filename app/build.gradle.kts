plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
}

android {
    namespace = libs.versions.applicationId.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

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
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":local"))
    implementation(project(":remote"))

    implementation(libs.bundles.android)
    implementation(libs.bundles.test)

    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hilt.compiler)

    implementation(libs.bundles.coroutines)

    implementation(platform(libs.compose))
    implementation(libs.bundles.compose)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.dataStore)

    implementation(libs.bundles.coil)

    implementation(libs.timber)
    implementation(libs.kakao)

    implementation(platform(libs.firebase))
    implementation(libs.bundles.firebase)
}

apply(plugin = "com.google.gms.google-services")