plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

kotlin {
    js {
        nodejs {
            binaries.executable()
        }
    }
    sourceSets {
        main {
            dependencies {
                implementation(npm("puppeteer", "8.0.0", false)) // dukat fails...
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
            }
        }
    }
}
