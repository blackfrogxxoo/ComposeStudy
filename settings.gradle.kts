pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ComposeStudy"
includeBuild("dependencies")

fun listFiles(dir: File) {
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
        println(it.absolutePath + ", " + name)
        include(name)
    }
    dir.listFiles().filter {
        it.isParentModule()
    }.forEach {
        listFiles(it)
    }
}

fun autoInclude() {
    val dir = rootDir
    listFiles(dir)
}

autoInclude()
