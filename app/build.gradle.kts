import me.wxc.deps.*

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
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

    autoImplProjects()
    addHilt()

    implementation(deps.androidx.ktx)
    implementation(deps.androidx.appCompat)

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

    addUnitTest()
    addAndroidTest()
}

fun Project.autoImplProjects() {
    val dir = rootDir
    dependencies {
        listFiles(dir) {
            implementation(project(it))
        }
    }
}


fun listFiles(dir: File, block: (String) -> Unit) {
    fun File.isParentModule() = isDirectory && listFiles().any { it.name == "settings.gradle" }
    fun File.isModule() = isDirectory && listFiles().any { it.name == "build.gradle.kts" }
    println("${dir.absolutePath} list files --->")
    dir.listFiles().filter {
        it.isModule()
    }.forEach {
        val name = if (it.parentFile.isParentModule()) {
            ":${it.parentFile.name}:${it.name}"
        } else {
            ":${it.name}"
        }
        if (name != ":app") {
            println(it.absolutePath + ", " + name)
            block(name)
        }
    }
    dir.listFiles().filter {
        it.isParentModule()
    }.forEach {
        listFiles(it, block)
    }
}
