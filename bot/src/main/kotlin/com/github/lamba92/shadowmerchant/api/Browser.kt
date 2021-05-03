package com.github.lamba92.shadowmerchant.api

import com.github.lamba92.shadowmerchant.api.puppeteer.PuppeteerBrowser
import puppeteer.Puppeteer
import puppeteer.ViewPort
import puppeteer.jsObject
import puppeteer.launch
import kotlin.time.Duration
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.DurationUnit.SECONDS
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
    suspend fun close()
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
    suspend fun isSelectorVisible(selector: String): Boolean
    suspend fun waitForSelector(
        selector: String,
        domVisible: Boolean = false,
        hidden: Boolean = false,
        timeout: Duration = 2.toDuration(SECONDS),
    ): Boolean

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
