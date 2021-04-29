package com.github.lamba92.shadowmerchant.api.puppeteer

import com.github.lamba92.shadowmerchant.api.Browser
import com.github.lamba92.shadowmerchant.api.Page
import kotlinx.coroutines.await
import puppeteer.navigate
import puppeteer.type
import kotlin.time.Duration
import kotlin.time.DurationUnit

class PuppeteerPage(val page: puppeteer.Page) : Page {
    override suspend fun navigateTo(url: String) {
        page.navigate(url)
    }

    override suspend fun type(selector: String, text: String, delayBetweenInputs: Duration) {
        page.type(selector, text, delayBetweenInputs.toInt(DurationUnit.MILLISECONDS))
    }

    override suspend fun click(selector: String) {
        page.click(selector).await()
    }

    override suspend fun waitForNavigation() {
        page.waitForNavigation().await()
    }

}

class PuppeteerBrowser(val browser: puppeteer.Browser) : Browser {
    override suspend fun openedPages() =
        browser.pages().await().toList().map { PuppeteerPage(it) }

    override suspend fun newPage() =
        PuppeteerPage(browser.newPage().await())
}
