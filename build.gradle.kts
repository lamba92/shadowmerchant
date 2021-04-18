plugins {
    // that's strange...
    kotlin("jvm") apply false
    kotlin("js") apply false
}

allprojects {
    group = "com.github.lamba92"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
    }
}
