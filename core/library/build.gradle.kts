plugins {
  id(libs.plugins.library.java.get().pluginId)
  alias(libs.plugins.jvm)
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}