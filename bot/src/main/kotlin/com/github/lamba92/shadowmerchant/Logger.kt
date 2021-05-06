package com.github.lamba92.shadowmerchant

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.js.Date

typealias LogTransform = (String) -> String
typealias LogAction = suspend (String) -> Unit

open class Logger(
    val name: String,
    private val logTransforms: Map<LogLevel, LogTransform> = LogLevel.values().associateWith { level ->
        { message -> "$name [$level] ${Date().toISOString()} | $message" }
    },
    private val logActions: List<LogAction> = listOf { println(it) },
    val logLevel: LogLevel? = null,
) {

    companion object {
        var globalLogLevel: LogLevel = LogLevel.INFO
    }

    enum class LogLevel {
        VERBOSE, DEBUG, INFO, WARN, ERROR, NONE
    }

    suspend fun log(message: String, level: LogLevel) {
        if (level < (logLevel ?: globalLogLevel)) {
            val finalMessage = logTransforms[level]?.invoke(message)
                ?: kotlin.error("Logger \"$name\" not configured for level $level")
            coroutineScope {
                logActions.forEach { action -> launch { action(finalMessage) } }
            }
        }
    }

    suspend fun verbose(message: String) =
        log(message, LogLevel.VERBOSE)

    suspend fun info(message: String) =
        log(message, LogLevel.INFO)

    suspend fun warn(message: String) =
        log(message, LogLevel.WARN)

    suspend fun debug(message: String) =
        log(message, LogLevel.DEBUG)

    suspend fun error(message: String) =
        log(message, LogLevel.ERROR)

}
