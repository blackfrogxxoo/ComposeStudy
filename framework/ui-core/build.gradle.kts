import me.wxc.deps.*

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "me.wxc.framework.uicore"
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
    api(deps.compose.activityCompose)
    api(platform(deps.compose.bom))
    api(deps.compose.ui)
    api(deps.compose.foundation)
    api(deps.compose.uiGraphics)
    api(deps.compose.uiToolingPreview)
    api(deps.compose.material3)
    debugApi(deps.compose.uiTooling)
    debugApi(deps.compose.uiTestManifest)
}