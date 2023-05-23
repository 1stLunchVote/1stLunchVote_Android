// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Versions.Android apply false
    id("com.android.library") version Versions.Android apply false
    id("org.jetbrains.kotlin.android") version Versions.Kotlin apply false
    id("com.google.dagger.hilt.android") version Versions.Hilt apply false
    id("org.jetbrains.kotlin.jvm") version Versions.Kotlin apply false
}

buildscript {
    dependencies{
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.Navigation}")
    }
}