plugins {
    // that's strange...
    kotlin("jvm") apply false
    kotlin("js") apply false
}

allprojects {
    group = "com.github.lamba92"
    version = "1.0-SNAPSHOT"
}
