import org.gradle.kotlin.dsl.DependencyHandlerScope

object Apps{
    const val CompileSdk = 33
    const val MinSdk = 26
    const val VersionCode = 1
    const val VersionName = "1.0.0"
    const val ApplicationId = "com.jwd.lunchvote"
}

object Versions{
    const val Android = "8.0.0"
    const val AppCompat = "1.6.1"
    const val Core = "1.10.1"
    const val Material = "1.9.0"
    const val ConstraintLayout = "2.1.4"
    const val Lifecycle = "2.6.1"
    const val Navigation = "2.5.3"
    const val Work = "2.8.1"
    const val RecyclerView = "1.3.0"

    // Hilt
    const val Hilt = "2.44"
    const val AndroidHilt = "1.0.0"

    // Kotlin, Coroutine
    const val Kotlin = "1.8.10"
    const val Coroutine = "1.6.4"

    // JavaX
    const val JavaX = "1"

    // Firebase
    const val Firebase = "32.0.0"

    // Retrofit
    const val Retrofit = "2.9.0"
    const val OkHttp = "4.9.1"

    // Compose
    const val Compose = "2023.01.00"
    const val Material3 = "1.0.1"
    const val ComposeNavigation = "2.5.1"
    const val ComposeHilt = "1.0.0"
    const val ComposeLifecycle = "2.6.1"

    // Test
    const val JUnit = "4.13.2"
    const val Ext = "1.1.5"
    const val Espresso = "3.5.1"
}

object Libraries{
    object AndroidX {
        const val AppCompat = "androidx.appcompat:appcompat:${Versions.AppCompat}"
        const val Core = "androidx.core:core-ktx:${Versions.Core}"
        const val Material = "com.google.android.material:material:${Versions.Material}"
        const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.ConstraintLayout}"
        const val Lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.Lifecycle}"
        const val LifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Lifecycle}"
        const val Navigation = "androidx.navigation:navigation-fragment-ktx:${Versions.Navigation}"
        const val NavigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.Navigation}"
        const val Work = "androidx.work:work-runtime-ktx:${Versions.Work}"
        const val RecyclerView = "androidx.recyclerview:recyclerview:${Versions.RecyclerView}"
    }

    object Coroutine {
        const val Coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Coroutine}"
        const val CoroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Coroutine}"
    }

    object Hilt{
        // Hilt
        const val Hilt = "com.google.dagger:hilt-android:${Versions.Hilt}"
        const val HiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.Hilt}"
        const val AndroidHilt = "androidx.hilt:hilt-common:${Versions.AndroidHilt}"
    }

    object Retrofit{
        const val Retrofit = "com.squareup.retrofit2:retrofit:${Versions.Retrofit}"
        const val OkHttp = "com.squareup.okhttp3:okhttp:${Versions.OkHttp}"
    }

    object Test{
        const val JUnit = "junit:junit:${Versions.JUnit}"
        const val Ext = "androidx.test.ext:junit:${Versions.Ext}"
        const val Espresso = "androidx.test.espresso:espresso-core:${Versions.Espresso}"
    }

    object JavaX{
        const val Inject = "javax.inject:javax.inject:${Versions.JavaX}"
    }

    object Compose{
        const val ComposeBom = "androidx.compose:compose-bom:${Versions.Compose}"
        const val Material3 = "androidx.compose.material3:material3:${Versions.Material3}"
        const val Foundation = "androidx.compose.foundation:foundation"
        const val UI = "androidx.compose.ui:ui"
        const val Tooling = "androidx.compose.ui:ui-tooling"
        const val Preview = "androidx.compose.ui:ui-tooling-preview"
        const val Navigation = "androidx.navigation:navigation-compose:${Versions.ComposeNavigation}"
        const val Hilt = "androidx.hilt:hilt-navigation-compose:${Versions.ComposeHilt}"
        const val Lifecycle = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.ComposeLifecycle}"
    }
}

fun DependencyHandlerScope.implementationAndroidX(){
    implementations(
        Libraries.AndroidX.AppCompat,
        Libraries.AndroidX.Core,
        Libraries.AndroidX.Material,
        Libraries.AndroidX.ConstraintLayout,
        Libraries.AndroidX.Lifecycle,
        Libraries.AndroidX.LifecycleViewModel,
        Libraries.AndroidX.Navigation,
        Libraries.AndroidX.NavigationUI,
        Libraries.AndroidX.Work,
        Libraries.AndroidX.RecyclerView
    )
}

fun DependencyHandlerScope.implementationCoroutine(){
    implementations(
        Libraries.Coroutine.Coroutine,
        Libraries.Coroutine.CoroutineAndroid
    )
}

fun DependencyHandlerScope.implementationHilt(){
    implementations(
        Libraries.Hilt.Hilt,
        Libraries.Hilt.AndroidHilt
    )
    kapts(
        Libraries.Hilt.HiltCompiler
    )
}

fun DependencyHandlerScope.implementationRetrofit(){
    implementations(
        Libraries.Retrofit.Retrofit,
        Libraries.Retrofit.OkHttp
    )
}

fun DependencyHandlerScope.implementationCompose(){
    implementations(
        platform(Libraries.Compose.ComposeBom),
        Libraries.Compose.Material3,
        Libraries.Compose.Foundation,
        Libraries.Compose.Preview,
        Libraries.Compose.Tooling,
        Libraries.Compose.UI,
        Libraries.Compose.Navigation,
        Libraries.Compose.Hilt,
        Libraries.Compose.Lifecycle
    )
}

fun DependencyHandlerScope.implementationTest(){
    add("testImplementation", Libraries.Test.JUnit)
    add("androidTestImplementation", Libraries.Test.Ext)
    add("androidTestImplementation", Libraries.Test.Espresso)
}

fun DependencyHandlerScope.implementations(vararg notations: Any) {
    notations.forEach {
        add("implementation", it)
    }
}

fun DependencyHandlerScope.kapts(vararg notations: Any) {
    notations.forEach {
        add("kapt", it)
    }
}