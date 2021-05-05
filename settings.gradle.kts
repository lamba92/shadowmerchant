@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "shadow-merchant"

pluginManagement {
    plugins {
        kotlin("js") version "1.5.0"
        kotlin("jvm") version "1.5.0"
        kotlin("plugin.serialization") version "1.5.0"
        id("com.github.node-gradle.node") version "3.0.1"
    }
}

include(":bot", ":puppeteer-externals")
