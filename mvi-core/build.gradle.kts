import me.wxc.deps.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "me.wxc.mvicore"
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
}

dependencies {
    initialMvi()
}