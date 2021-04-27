plugins {
    kotlin("js")
}

kotlin {
    js(IR) {
        compilations.all {
            kotlinOptions {
                sourceMap = true
            }
        }
        nodejs()
        useCommonJs()
    }
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
        main {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
                api("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
                api(npm("puppeteer", "8.0.0"))
                api(npm("bufferutil", "4.0.3"))
                api(npm("utf-8-validate", "5.0.4"))
            }
        }
    }
}
