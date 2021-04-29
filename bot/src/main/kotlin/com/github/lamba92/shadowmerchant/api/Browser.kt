package com.github.lamba92.shadowmerchant.api

import com.github.lamba92.shadowmerchant.api.puppeteer.PuppeteerBrowser
import puppeteer.Puppeteer
import puppeteer.ViewPort
import puppeteer.jsObject
import puppeteer.launch
import kotlin.time.Duration


interface Browser {

    companion object {
        suspend fun launch(
            headless: Boolean = true,
            viewPort: ViewPortSize?
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
    suspend fun type(selector: String, text: String, delayBetweenInputs: Duration)
    suspend fun click(selector: String)
    suspend fun waitForNavigation()
}

data class ViewPortSize(val width: Int, val height: Int)
