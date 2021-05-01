package com.github.lamba92.shadowmerchant.api

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

typealias PageAction<T> = suspend Page.() -> T

class PageExecutor<T>(private val pages: List<Page>) : CoroutineScope {

    private val channel: Channel<Execution<T>> = Channel(pages.size)

    private data class Execution<T>(val action: PageAction<T>, val result: CompletableDeferred<T>)

    override val coroutineContext = SupervisorJob()

    init {
        for (page in pages) {
            launch {
                while (!channel.isClosedForSend) {
                    val execution = channel.receive()
                    val result = execution.action.invoke(page)
                    execution.result.complete(result)
                }
            }
        }
    }

    suspend fun offerExecution(action: PageAction<T>): CompletableDeferred<T> =
        CompletableDeferred<T>().also { channel.send(Execution(action, it)) }

    suspend fun dispose(cause: Throwable? = null): Boolean {
        val result = channel.close(cause)
        if (cause != null) {
            coroutineContext.cancel(CancellationException(cause.message, cause))
        } else {
            coroutineContext.join()
        }
        coroutineScope {
            pages.forEach { launch { it.close() } }
        }
        return result
    }
}

fun <T> List<Page>.asPageExecutor() = PageExecutor<T>(this)

suspend fun <T> Browser.newPageExecutor(pages: Int) = newPages(pages).asPageExecutor<T>()

suspend inline fun <T, R> PageExecutor<T>.use(action: (PageExecutor<T>) -> R) =
    try {
        action(this)
    } finally {
        dispose()
    }
