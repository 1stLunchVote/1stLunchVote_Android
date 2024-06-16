plugins {
  alias(libs.plugins.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
  alias(libs.plugins.secrets)
}

android {
  namespace = libs.versions.applicationId.get() + ".presentation"
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
  implementation(project(":core:config"))
  implementation(project(":core:common"))
  implementation(project(":core:ui"))
  implementation(project(":core:test"))
  implementation(project(":domain"))
  implementation(project(":data"))
  implementation(project(":local"))

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
  implementation(libs.firebase.dynamicLinks)
  implementation(libs.firebase.auth)
  implementation(libs.firebase.gmsAuth)
  implementation(libs.firebase.functions)
}