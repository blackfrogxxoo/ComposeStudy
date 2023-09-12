// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.1.1" apply false
    kotlin("android") version "1.8.10" apply false
    kotlin("jvm") version "1.8.10" apply false
    kotlin("plugin.serialization") version "1.8.20" apply false
    id("com.google.devtools.ksp") version "1.8.20-1.0.10" apply false
    id("me.wxc.deps")
    id("com.google.dagger.hilt.android") version "2.44" apply false
}