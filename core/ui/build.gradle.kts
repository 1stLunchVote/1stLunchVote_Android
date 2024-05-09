plugins {
  alias(libs.plugins.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.ksp)
}

android {
  namespace = libs.versions.applicationId.get() + ".core.ui"
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
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

  buildFeatures {
    compose = true
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
}

dependencies {
  implementation(libs.bundles.android)
  implementation(libs.bundles.test)
  implementation(libs.bundles.coroutines)

  implementation(platform(libs.compose))
  implementation(libs.bundles.compose)
}