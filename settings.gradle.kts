import java.util.Properties

rootProject.name = "P24 SDK Demo"
include(":app")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            val properties = readProperties(File(rootDir, "local.properties"))
            url = uri("https://maven.pkg.github.com/przelewy24/p24-sdk-android")
            credentials {
                username = properties["github.user"] as String?
                password = properties["github.token"] as String?
            }
        }
    }
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}