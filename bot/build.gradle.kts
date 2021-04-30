import com.github.gradle.node.task.NodeTask
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Mode
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Mode.DEVELOPMENT
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Mode.PRODUCTION
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Target.NODE
import com.github.lamba92.kotlingram.gradle.tasks.outputBundleFile
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.RootPackageJsonTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import kotlin.io.path.absolute

plugins {
    kotlin("js")
    kotlin("plugin.serialization")
    id("com.github.node-gradle.node")
}

kotlin {
    js(IR) {
        nodejs()
        compilations.all {
            kotlinOptions {
                sourceMap = true
            }
        }
        useCommonJs()
        binaries.executable()
    }
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
        }
        main {
            dependencies {
                implementation(projects.puppeteerExternals)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
                implementation(npm("tmp", "0.2.1"))
                implementation(npm("@types/tmp", "0.2.0"))
                implementation(npm("fs-extra", "9.1.0"))
                implementation(npm("@types/fs-extra", "9.0.11"))
                implementation(devNpm("webpack-cli", "4.6.0"))
                implementation(devNpm("terser-webpack-plugin", "5.1.1"))
                implementation(devNpm("webpack", "5.33.2"))
            }
        }
    }
}

tasks {
    named<NodeJsExec>("nodeRun") {
        environment["STORES_DIR"] = rootDir.toPath().resolve("stores").toAbsolutePath().toString()
    }
}

//val rootPackageJson by rootProject.tasks.getting(RootPackageJsonTask::class)
//
//node {
//    download.set(true)
//}
//
//operator fun File.div(child: String) =
//    File(this, child)
//
//@Suppress("UnstableApiUsage")
//fun NodeTask.setNodeModulesPath(path: String) =
//    environment.put("NODE_PATH", path)
//
//@Suppress("UnstableApiUsage")
//fun NodeTask.setNodeModulesPath(folder: File) =
//    environment.put("NODE_PATH", folder.normalize().absolutePath)
//
//tasks {
//
//    fun generateTasks(task: Task, file: File, type: String) {
//        Mode.values()
//            .map { it to it.name.toLowerCase().capitalize() }
//            .forEach { (mode, modeName) ->
//                val generateWebpackConfig =
//                    create<GenerateWebpackConfig>("generate${type.capitalize()}${modeName}WebpackConfig") {
//                        dependsOn(task)
//                        group = "other"
//                        target = NODE
//                        this.mode = mode // PRODUCTION will fail
//                        entryFile = file
//                        modulesFolder.set(listOf(rootPackageJson.rootPackageJson.parentFile / "node_modules"))
//                        outputBundleName = buildString {
//                            append(project.name)
//                            when (mode) {
//                                PRODUCTION -> append("-prod")
//                                DEVELOPMENT -> append("-dev")
//                            }
//                            append(".js")
//                        }
//                        outputBundleFolder = file("$buildDir/distributions").absolutePath
//                        outputConfig = file("$buildDir/webpack/webpack.${modeName.toLowerCase()}.js")
//                    }
//
//                val webpackExecutable = create<NodeTask>("${type}${modeName.capitalize()}WebpackExecutable") {
//                    group = "distribution"
//                    dependsOn(generateWebpackConfig)
//                    script.set(rootPackageJson.rootPackageJson.parentFile / "node_modules/webpack-cli/bin/cli.js")
//                    args.set(listOf("-c", generateWebpackConfig.outputConfig.absolutePath))
//
//
//                    setNodeModulesPath(rootPackageJson.rootPackageJson.parentFile / "node_modules")
//
////                inputs.file(generateWebpackConfig.outputConfig)
////                inputs.file(fixNodeFetchForWebpack.destinationDir / "fixed.js")
////                outputs.file(generateWebpackConfig.outputBundleFile)
//                }
//
//                register<NodeTask>("run${type.capitalize()}${modeName}WebpackExecutable") {
//                    group = "application"
//                    dependsOn(webpackExecutable)
//                    script.set(generateWebpackConfig.outputBundleFile)
//                }
//            }
//    }
//    val compileKotlinJsLegacy by getting(Kotlin2JsCompile::class)
//    val compileTestProductionExecutableKotlinJsIr by getting(KotlinJsIrLink::class)
//
//        generateTasks(compileKotlinJsLegacy, compileKotlinJsLegacy.outputFile, "legacy")
//
//        generateTasks(compileTestProductionExecutableKotlinJsIr, compileTestProductionExecutableKotlinJsIr.outputFile, "ir")
//
//}
