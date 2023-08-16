import me.wxc.deps.*

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "me.wxc.framework.pullrefresh"
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
    implementation(project(":framework:ui-core"))
}