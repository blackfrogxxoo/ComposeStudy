package me.wxc.deps

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object appConfig {
    const val namespace = "me.wxc.composestudy"
    const val applicationId = "me.wxc.composestudy"

    const val compileSdkVersion = 33
    const val minSdkVersion = 24
    const val targetSdkVersion = 33

    val javaVersion = JavaVersion.VERSION_17

    private const val MAJOR = 1
    private const val MINOR = 0
    private const val PATCH = 0
    const val versionCode = MAJOR * 10000 + MINOR * 100 + PATCH
    const val versionName = "$MAJOR.$MINOR.$PATCH"
}

object deps {

    object androidx {
        const val ktx = "androidx.core:core-ktx:1.10.1"
        const val appCompat = "androidx.appcompat:appcompat:1.6.0"
    }

    object compose {
        const val version = "1.4.5"

        const val activityCompose = "androidx.activity:activity-compose:1.7.0"
        const val bom = "androidx.compose:compose-bom:2023.03.00"
        const val ui = "androidx.compose.ui:ui"
        const val foundation = "androidx.compose.foundation:foundation"
        const val uiGraphics = "androidx.compose.ui:ui-graphics"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val material3 = "androidx.compose.material3:material3"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
    }

    object lifecycle {
        private const val version = "2.6.1"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version" // viewModelScope
        const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version" // lifecycleScope
        const val compose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
    }

    object coroutines {
        private const val version = "1.6.0"

        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object koin {
        private const val version = "3.1.5"

        const val core = "io.insert-koin:koin-core:$version"
        const val android = "io.insert-koin:koin-android:$version"
    }

    object room {
        private const val version = "2.5.0"

        const val core = "androidx.room:room-runtime:$version"
        const val ksp = "androidx.room:room-compiler:$version"
        const val ktx = "androidx.room:room-ktx:$version"
    }

    object ktor {
        private const val version = "2.3.3"

        const val core = "io.ktor:ktor-client-core:$version"
        const val cio = "io.ktor:ktor-client-cio:$version"
        const val negotiation = "io.ktor:ktor-client-content-negotiation:$version"
        const val json = "io.ktor:ktor-serialization-kotlinx-json:$version"
    }

    object coil {
        private const val version = "2.4.0"

        const val core = "io.coil-kt:coil-compose:$version"
    }

    object test {
        const val junit = "junit:junit:4.13.2"
        const val extJunit = "androidx.test.ext:junit:1.1.5"
        object espresso {
            const val espresso = "androidx.test.espresso:espresso-core:3.5.1"
        }
        object ui {
            const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
        }
    }
}

private typealias PDsS = PluginDependenciesSpec
private typealias PDS = PluginDependencySpec

inline val PDsS.androidApplication: PDS get() = id("com.android.application")
inline fun PDsS.androidApplication(): PDS = id("com.android.application")
inline val PDsS.androidLib: PDS get() = id("com.android.library")
inline val PDsS.jetbrainsKotlinAndroid: PDS get() = id("org.jetbrains.kotlin.android")
inline val PDsS.kotlin: PDS get() = id("kotlin")
inline val PDsS.kotlinParcelize: PDS get() = id("kotlin-parcelize")
//inline val PDsS.kotlinSerialization: PDS get() = kotlin("plugin.serialization").version("1.8.20")
inline val PDsS.ksp: PDS get() = id("com.google.devtools.ksp").version("1.8.20-1.0.10")

//inline val DependencyHandler.mviCore get() = project(":mvi-core")
//inline val DependencyHandler.data get() = project(":data")
//inline val DependencyHandler.utils get() = project(":utils")

fun DependencyHandler.addUnitTest() {
    val configName = "testImplementation"
    add(configName, deps.test.junit)
}

fun DependencyHandler.addAndroidTest() {
    val configName = "androidTestImplementation"
    add(configName, deps.test.extJunit)
    add(configName, deps.test.espresso.espresso)
    add(configName, deps.test.ui.uiTestJunit4)
    add(configName, platform(deps.compose.bom))
}

fun DependencyHandler.initialMvi() {
    val configName = "implementation"
    add(configName, deps.androidx.ktx)
    add(configName, deps.androidx.appCompat)
    add(configName, deps.lifecycle.viewModelKtx)
    add(configName, deps.lifecycle.runtimeKtx)
    add(configName, deps.coroutines.core)
    add(configName, deps.coroutines.android)
}