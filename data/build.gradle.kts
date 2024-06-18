plugins {
  alias(libs.plugins.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = libs.versions.applicationId.get() + ".data"
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
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  implementation(project(":domain"))
  implementation(project(":core:config"))
  implementation(project(":core:common"))

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

  implementation(platform(libs.firebase))
  implementation(libs.bundles.firebase)

  implementation(libs.timber)
}