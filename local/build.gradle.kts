plugins {
  alias(libs.plugins.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = libs.versions.applicationId.get() + ".local"
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()

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

  buildFeatures {
    buildConfig = true
  }
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  implementation(project(":data"))
  implementation(project(":core:config"))
  implementation(project(":core:common"))

  implementation(libs.bundles.android)

  implementation(libs.bundles.hilt)
  ksp(libs.bundles.hilt.compiler)

  implementation(libs.bundles.room)
  ksp(libs.room.compiler)
  implementation(libs.dataStore)

  implementation(libs.timber)

  implementation(libs.firebase.auth)
}