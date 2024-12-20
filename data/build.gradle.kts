plugins {
  id(libs.plugins.library.java.get().pluginId)
  alias(libs.plugins.jvm)
  alias(libs.plugins.ksp)
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
  implementation(project(":core:library"))
  implementation(project(":core:config"))
  implementation(project(":domain"))

  implementation(libs.coroutines)
  implementation(libs.inject)
}