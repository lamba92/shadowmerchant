plugins {
    kotlin("js")
}

kotlin {
    js {
        nodejs()
    }
    sourceSets {
        main {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
                api(npm("puppeteer-core", "8.0.0"))
            }
        }
    }
}
