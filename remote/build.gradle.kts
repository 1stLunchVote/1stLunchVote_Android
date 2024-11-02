plugins {
  alias(libs.plugins.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = libs.versions.applicationId.get() + ".remote"
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
  implementation(project(":core:library"))
  implementation(project(":core:config"))
  implementation(project(":data"))

  implementation(libs.timber)

  implementation(libs.bundles.android)
  implementation(libs.bundles.test)

  implementation(libs.bundles.hilt)
  ksp(libs.bundles.hilt.compiler)

  implementation(platform(libs.firebase))
  implementation(libs.bundles.firebase)
}