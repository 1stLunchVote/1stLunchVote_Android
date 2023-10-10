// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Versions.Android apply false
    id("com.android.library") version Versions.Android apply false
    id("org.jetbrains.kotlin.android") version Versions.Kotlin apply false
    id("com.google.dagger.hilt.android") version Versions.Hilt apply false
    id("org.jetbrains.kotlin.jvm") version Versions.Kotlin apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version Versions.Secret apply false
    id("com.google.devtools.ksp") version Versions.Ksp apply false
}

buildscript {
    dependencies{
        classpath("com.google.gms:google-services:${Versions.Gms}")
        classpath("com.android.tools.build:gradle:8.0.2")
    }
}