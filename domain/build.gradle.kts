plugins {
    id("java-library")
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies{
    implementations(
        Libraries.Coroutine.Coroutine,
        Libraries.JavaX.Inject
    )
}