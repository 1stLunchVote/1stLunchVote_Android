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
    const val RecyclerView = "1.3.0"
    const val Work = "2.8.1"
    const val SplashScreen = "1.0.0"
    const val Timber = "4.7.1"
    const val Secret = "2.0.1"

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
    const val Gms = "4.3.15"
    const val GmsAuth = "20.4.0"

    // Retrofit
    const val Retrofit = "2.9.0"
    const val OkHttp = "4.9.1"

    // Room
    const val Room = "2.5.1"

    // Compose
    const val Compose = "2023.04.00"
    const val Material3 = "1.0.1"
    const val ComposeNavigation = "2.5.1"
    const val ComposeHilt = "1.0.0"
    const val ComposeLifecycle = "2.6.1"
    const val ComposeConstraint = "1.0.1"

    // Test
    const val JUnit = "4.13.2"
    const val Ext = "1.1.5"
    const val Espresso = "3.5.1"
    const val Mockk ="1.13.5"
    const val TestCore = "1.5.0"
    const val Slf4j = "2.0.7"

    const val Coil = "2.4.0"
    const val Kakao = "2.14.0"
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
        const val SplashScreen = "androidx.core:core-splashscreen:${Versions.SplashScreen}"
    }

    object Coroutine {
        const val Coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Coroutine}"
        const val CoroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Coroutine}"
    }


    object Firebase{
        const val Firebase = "com.google.firebase:firebase-bom:${Versions.Firebase}"
        const val Auth = "com.google.firebase:firebase-auth-ktx"
        const val DataBase = "com.google.firebase:firebase-database-ktx"
        const val GmsAuth = "com.google.android.gms:play-services-auth:${Versions.GmsAuth}"
        const val Function = "com.google.firebase:firebase-functions-ktx"
    }

    object Hilt{
        // Hilt
        const val Hilt = "com.google.dagger:hilt-android:${Versions.Hilt}"
        const val HiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.Hilt}"
        const val AndroidHiltCompiler = "androidx.hilt:hilt-compiler:${Versions.AndroidHilt}"
        const val AndroidHilt = "androidx.hilt:hilt-common:${Versions.AndroidHilt}"
        const val HiltWork = "androidx.hilt:hilt-work:${Versions.AndroidHilt}"
    }

    object Retrofit{
        const val Retrofit = "com.squareup.retrofit2:retrofit:${Versions.Retrofit}"
        const val OkHttp = "com.squareup.okhttp3:okhttp:${Versions.OkHttp}"
    }

    object Test{
        const val JUnit = "junit:junit:${Versions.JUnit}"
        const val Ext = "androidx.test.ext:junit:${Versions.Ext}"
        const val Espresso = "androidx.test.espresso:espresso-core:${Versions.Espresso}"
        const val Mockk = "io.mockk:mockk:${Versions.Mockk}"
        const val AndroidMockk = "io.mockk:mockk-android:${Versions.Mockk}"
        const val TestCore = "androidx.test:core-ktx:${Versions.TestCore}"
        const val CoroutineTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Coroutine}"
        const val Slf4j = "org.slf4j:slf4j-simple:${Versions.Slf4j}"
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
        const val ConstraintLayout = "androidx.constraintlayout:constraintlayout-compose:${Versions.ComposeConstraint}"
    }

    object Room{
        const val Room = "androidx.room:room-ktx:${Versions.Room}"
        const val RoomCompiler = "androidx.room:room-compiler:${Versions.Room}"
        const val RoomRuntime = "androidx.room:room-runtime:${Versions.Room}"
        const val RoomPaging = "androidx.room:room-paging:${Versions.Room}"
    }


    const val Kakao = "com.kakao.sdk:v2-all-rx:${Versions.Kakao}"
    const val Timber = "com.jakewharton.timber:timber:${Versions.Timber}"
    const val Coil = "io.coil-kt:coil-compose:${Versions.Coil}"
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
        Libraries.AndroidX.SplashScreen
    )
}

fun DependencyHandlerScope.implementationCoroutine(){
    implementations(
        Libraries.Coroutine.Coroutine,
        Libraries.Coroutine.CoroutineAndroid
    )
}


fun DependencyHandlerScope.implementationFirebase(){
    implementations(
        platform(Libraries.Firebase.Firebase)
    )
}

fun DependencyHandlerScope.implementationHilt(){
    implementations(
        Libraries.Hilt.Hilt,
        Libraries.Hilt.AndroidHilt,
        Libraries.Hilt.HiltWork
    )
    kapts(
        Libraries.Hilt.HiltCompiler,
        Libraries.Hilt.AndroidHiltCompiler
    )
}


fun DependencyHandlerScope.implementationRetrofit(){
    implementations(
        Libraries.Retrofit.Retrofit,
        Libraries.Retrofit.OkHttp
    )
}

fun DependencyHandlerScope.implementationRoom(){
    implementations(
        Libraries.Room.Room,
        Libraries.Room.RoomRuntime,
        Libraries.Room.RoomPaging
    )
    kapts(
        Libraries.Room.RoomCompiler
    )
}

fun DependencyHandlerScope.implementationCompose(){
    implementations(
        platform(Libraries.Compose.ComposeBom),
        Libraries.Compose.Material3,
        Libraries.Compose.Foundation,
//        Libraries.Compose.Preview,
        Libraries.Compose.Tooling,
        Libraries.Compose.UI,
        Libraries.Compose.Navigation,
        Libraries.Compose.Hilt,
        Libraries.Compose.Lifecycle,
        Libraries.Compose.ConstraintLayout
    )
}

fun DependencyHandlerScope.implementationTest(){
    add("testImplementation", Libraries.Test.JUnit)
    add("androidTestImplementation", Libraries.Test.Ext)
    add("androidTestImplementation", Libraries.Test.Espresso)
    add("testImplementation", Libraries.Test.TestCore)
    add("testImplementation", Libraries.Test.CoroutineTest)
    add("testImplementation", Libraries.Test.Mockk)
    add("androidTestImplementation", Libraries.Test.AndroidMockk)
    add("testImplementation", Libraries.Test.Slf4j)
}

fun DependencyHandlerScope.apiTest(){
    add("api", Libraries.Test.JUnit)
    add("api", Libraries.Test.Ext)
    add("api", Libraries.Test.Espresso)
    add("api", Libraries.Test.TestCore)
    add("api", Libraries.Test.CoroutineTest)
    add("api", Libraries.Test.Mockk)
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