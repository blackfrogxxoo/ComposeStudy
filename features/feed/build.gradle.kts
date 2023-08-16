import me.wxc.deps.*

plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    kotlin("android")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "me.wxc.features.feed"
    compileSdk = appConfig.compileSdkVersion

    defaultConfig {
        minSdk = appConfig.minSdkVersion
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(project(":framework:mvi-core"))
    implementation(project(":framework:http"))
    implementation(project(":framework:pullrefresh"))
    implementation(project(":framework:ui-core"))

    addHilt()

    implementation(deps.androidx.ktx)

    implementation(deps.lifecycle.viewModelKtx)
    implementation(deps.lifecycle.runtimeKtx)
    implementation(deps.lifecycle.compose)
    implementation(deps.lifecycle.runtimeCompose)

    implementation(deps.coroutines.core)
    implementation(deps.coroutines.android)

    implementation(deps.room.core)
    implementation(deps.room.ktx)
    ksp(deps.room.ksp)

    implementation(deps.coil.core)
}