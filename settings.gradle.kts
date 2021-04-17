rootProject.name = "shadow-merchant"

pluginManagement {
    plugins {
        kotlin("js") version "1.5.0-RC"
        kotlin("jvm") version "1.5.0-RC"
        kotlin("plugin.serialization") version "1.5.0-RC"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
    }
}

include(":bot", ":puppeteer-core-externals")
