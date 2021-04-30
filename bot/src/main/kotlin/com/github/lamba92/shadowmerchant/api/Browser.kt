package com.github.lamba92.shadowmerchant.api

import com.github.lamba92.shadowmerchant.api.puppeteer.PuppeteerBrowser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import puppeteer.Puppeteer
import puppeteer.ViewPort
import puppeteer.jsObject
import puppeteer.launch
import kotlin.time.Duration
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.toDuration


interface Browser {

    companion object {
        suspend fun launch(
            headless: Boolean = true,
            viewPort: ViewPortSize? = ViewPortSize(800, 600),
        ): Browser = PuppeteerBrowser(
            Puppeteer.launch {
                this.headless = headless
                if (viewPort != null) this.defaultViewport = jsObject<ViewPort>().apply {
                    width = viewPort.width
                    height = viewPort.height
                }
                else defaultViewport = null
            }
        )
    }

    suspend fun openedPages(): List<Page>
    suspend fun newPage(): Page
}

interface Page {
    suspend fun navigateTo(url: String)
    suspend fun type(selector: String, text: String, delayBetweenInputs: Duration = 2.toDuration(MILLISECONDS))
    suspend fun click(
        selector: String,
        waitForNavigation: Boolean = false,
        waitForNavigationOption: WaitForNavigationOption = WaitForNavigationOption.LOAD,
    )

    suspend fun close(runBeforeUnloadEvent: Boolean = false)
    suspend fun waitForNavigation(option: WaitForNavigationOption = WaitForNavigationOption.LOAD)
}

enum class WaitForNavigationOption {
    LOAD, DOM_CONTENT_LOADED, NETWORK_IDLE_1, NETWORK_IDLE_2
}

data class ViewPortSize(val width: Int, val height: Int)

suspend fun Browser.newPages(number: Int) = buildList {
    repeat(number) {
        add(newPage())
    }
}

typealias PageAction = suspend Page.() -> Unit

class PageExecutor private constructor(
    val pages: List<Page>,
    private val channel: Channel<PageAction> = Channel(pages.size),
) : SendChannel<PageAction> by channel, CoroutineScope {

    companion object {
        operator fun invoke(pages: List<Page>) = PageExecutor(pages)
    }

    override val coroutineContext = SupervisorJob()

    init {
        for (page in pages) {
            launch {
                while (!channel.isClosedForSend)
                    channel.receive().invoke(page)
            }
        }
    }

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

fun List<Page>.asPageExecutor() = PageExecutor(this)

suspend fun Browser.newPageExecutor(pages: Int) = newPages(pages).asPageExecutor()

suspend inline fun <R> PageExecutor.use(action: (PageExecutor) -> R) =
    try {
        action(this)
    } finally {
        dispose()
    }
