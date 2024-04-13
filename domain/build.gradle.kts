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
  implementation(libs.coroutines)
  implementation(libs.inject)
}