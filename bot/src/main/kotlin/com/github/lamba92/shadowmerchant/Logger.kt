package com.github.lamba92.shadowmerchant

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.js.Date

typealias LogTransform = (String) -> String
typealias LogAction = suspend (String) -> Unit

open class Logger(
    val name: String,
    private val logTransforms: Map<LogLevel, LogTransform>,
    private val logActions: List<LogAction>
) {

    companion object Default : Logger(
        "DefaultLogger",
        LogLevel.values().associateWith { level ->
            { message ->
                "[$level] ${Date().toISOString()} | $message"
            }
        },
        listOf { println(it) }
    )

    enum class LogLevel {
        VERBOSE, INFO, WARN, DEBUG, ERROR
    }

    suspend fun log(message: String, level: LogLevel) {
        val finalMessage = logTransforms[level]?.invoke(message)
            ?: kotlin.error("Logger \"$name\" not configured for level $level")

        coroutineScope {
            logActions.forEach { action -> launch { action(finalMessage) } }
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
