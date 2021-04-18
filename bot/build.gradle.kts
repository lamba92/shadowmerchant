import com.github.gradle.node.task.NodeTask
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Mode
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Mode.DEVELOPMENT
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Mode.PRODUCTION
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Target.NODE
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.TerserPluginSettings
import com.github.lamba92.kotlingram.gradle.tasks.outputBundleFile
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.RootPackageJsonTask
import java.util.*

plugins {
    kotlin("js")
    kotlin("plugin.serialization")
    id("com.github.node-gradle.node")
}

kotlin {
    js(LEGACY) {
        nodejs {
            binaries.executable()
        }
        compilations.all {
            kotlinOptions {
                sourceMap = true
            }
        }
    }
    sourceSets {
        main {
            dependencies {
                implementation(projects.puppeteerExternals)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
                implementation(devNpm("webpack-cli", "4.6.0"))
                implementation(devNpm("terser-webpack-plugin", "5.1.1"))
                implementation(devNpm("webpack", "5.33.2"))
            }
        }
    }
}

val rootPackageJson by rootProject.tasks.getting(RootPackageJsonTask::class)

node {
    download.set(true)
}

operator fun File.div(child: String) =
    File(this, child)

@Suppress("UnstableApiUsage")
fun NodeTask.setNodeModulesPath(path: String) =
    environment.put("NODE_PATH", path)

@Suppress("UnstableApiUsage")
fun NodeTask.setNodeModulesPath(folder: File) =
    environment.put("NODE_PATH", folder.normalize().absolutePath)

//tasks {
//
//    val compileProductionExecutableKotlinJs by getting(KotlinJsIrLink::class)
//
//    val fixNodeFetchForWebpack by creating(Copy::class) {
//        dependsOn(compileProductionExecutableKotlinJs)
//        from(compileProductionExecutableKotlinJs.outputFile) {
//            filter { line ->
//                line.replace(
//                    "tmp = jsRequireNodeFetch()(input, init);",
//                    "tmp = jsRequireNodeFetch().default(input, init);"
//                )
//            }
//            rename { "fixed.js" }
//        }
//        into("$buildDir/fix")
//    }
//
//    Mode.values()
//        .map { it to it.name.toLowerCase().capitalize() }
//        .forEach { (mode, modeName) ->
//            val generateWebpackConfig =
//                create<GenerateWebpackConfig>("generate${modeName}WebpackConfig") {
//                    dependsOn(fixNodeFetchForWebpack)
//                    group = "other"
//                    target = NODE
//                    this.mode = mode // PRODUCTION will fail
//                    entryFile = fixNodeFetchForWebpack.destinationDir / "fixed.js"
//                    modulesFolder.set(listOf(rootPackageJson.rootPackageJson.parentFile / "node_modules"))
//                    outputBundleName = buildString {
//                        append(project.name)
//                        when (mode) {
//                            PRODUCTION -> append("-prod")
//                            DEVELOPMENT -> append("-dev")
//                        }
//                        append(".js")
//                    }
//                    outputBundleFolder = file("$buildDir/distributions").absolutePath
//                    outputConfig = file("$buildDir/webpack/webpack.${modeName.toLowerCase()}.js")
//                    if (mode == PRODUCTION)
//                        terserSettings.set(
//                            TerserPluginSettings(
//                                parallel = true,
//                                terserOptions = TerserPluginSettings.Options(
//                                    mangle = true,
//                                    sourceMaps = false,
//                                    keepClassnames = Regex("AbortSignal"),
//                                    keepFileNames = Regex("AbortSignal")
//                                )
//                            )
//                        )
//                }
//
//            val webpackExecutable = create<NodeTask>("${modeName.toLowerCase()}WebpackExecutable") {
//                group = "distribution"
//                dependsOn(generateWebpackConfig)
//                script.set(rootPackageJson.rootPackageJson.parentFile / "node_modules/webpack-cli/bin/cli.js")
//                args.set(listOf("-c", generateWebpackConfig.outputConfig.absolutePath))
//
//
//                setNodeModulesPath(rootPackageJson.rootPackageJson.parentFile / "node_modules")
//
////                inputs.file(generateWebpackConfig.outputConfig)
////                inputs.file(fixNodeFetchForWebpack.destinationDir / "fixed.js")
////                outputs.file(generateWebpackConfig.outputBundleFile)
//            }
//
//            register<NodeTask>("run${modeName}WebpackExecutable") {
//                group = "application"
//                dependsOn(webpackExecutable)
//                script.set(generateWebpackConfig.outputBundleFile)
//                rootProject.file("local.properties")
//                    .takeIf { it.exists() && it.isFile }
//                    ?.bufferedReader()
//                    ?.use {
//                        Properties().apply { load(it) }
//                            .entries.toList()
//                            .associate { it.key.toString() to it.value.toString() }
//                            .let {
//                                @Suppress("UnstableApiUsage")
//                                environment.putAll(it)
//                            }
//                    }
//            }
//        }
////    register<NodeTask>("run") {
////        group = "application"
////        dependsOn(compileProductionExecutableKotlinJs)
////        script.set(compileProductionExecutableKotlinJs.outputFile)
////        setNodeModulesPath(rootPackageJson.rootPackageJson.parentFile / "node_modules")
////    }
//}
