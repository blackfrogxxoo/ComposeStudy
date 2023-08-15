import me.wxc.deps.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization").version("1.8.20")
}

android {
    namespace = appConfig.namespace
    compileSdk = appConfig.compileSdkVersion

    defaultConfig {
        applicationId = appConfig.applicationId
        minSdk = appConfig.minSdkVersion
        targetSdk = appConfig.targetSdkVersion
        versionCode = appConfig.versionCode
        versionName = appConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = appConfig.javaVersion
        targetCompatibility = appConfig.javaVersion
    }
    kotlinOptions {
        jvmTarget = appConfig.javaVersion.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = deps.compose.version
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(
        fileTree(
            mapOf(
                "dir" to "libs",
                "include" to listOf("*.jar")
            )
        )
    )

    implementation(project(":mvi-core"))
    implementation(project(":pullrefresh"))

    implementation(deps.androidx.ktx)

    implementation(deps.lifecycle.viewModelKtx)
    implementation(deps.lifecycle.runtimeKtx)
    implementation(deps.lifecycle.compose)

    implementation(deps.coroutines.core)
    implementation(deps.coroutines.android)

    implementation(deps.koin.core)
    implementation(deps.koin.android)

    implementation(deps.room.core)
    implementation(deps.room.ktx)
    ksp(deps.room.ksp)

    implementation(deps.ktor.core)
    implementation(deps.ktor.cio)
    implementation(deps.ktor.negotiation)
    implementation(deps.ktor.json)

    implementation(deps.compose.activityCompose)
    implementation(platform(deps.compose.bom))
    implementation(deps.compose.ui)
    implementation(deps.compose.foundation)
    implementation(deps.compose.uiGraphics)
    implementation(deps.compose.uiToolingPreview)
    implementation(deps.compose.material3)
    debugImplementation(deps.compose.uiTooling)
    debugImplementation(deps.compose.uiTestManifest)

    implementation(deps.coil.core)

    addUnitTest()
    addAndroidTest()
}