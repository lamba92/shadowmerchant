package com.github.lamba92.shadowmerchant.api.puppeteer

import com.github.lamba92.shadowmerchant.api.Browser
import com.github.lamba92.shadowmerchant.api.Page
import com.github.lamba92.shadowmerchant.api.WaitForNavigationOption
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import puppeteer.*
import kotlin.time.Duration
import kotlin.time.DurationUnit.MILLISECONDS

class PuppeteerPage(private val page: puppeteer.Page) : Page {
    override suspend fun navigateTo(url: String) {
        page.navigate(url)
    }

    override suspend fun type(selector: String, text: String, delayBetweenInputs: Duration) {
        page.type(selector, text, delayBetweenInputs.toInt(MILLISECONDS))
    }

    override suspend fun click(
        selector: String,
        waitForNavigation: Boolean,
        waitForNavigationOption: WaitForNavigationOption,
    ) = coroutineScope {

        if (waitForNavigation)
            launch { waitForNavigation(waitForNavigationOption) }

        page.click(selector).await()
    }

    override suspend fun close(runBeforeUnloadEvent: Boolean) {
        if (runBeforeUnloadEvent)
            page.close(jsObject<CloseOptions>().apply { runBeforeUnload = runBeforeUnloadEvent }).await()
        else
            page.close().await()
    }

    override suspend fun waitForNavigation(option: WaitForNavigationOption) {
        page.waitForNavigation(jsObject<WaitForNavigationOptions>().also {
            it.waitUntil = option.name.toLowerCase().split("_").joinToString("")
        }).await()
    }

    override suspend fun isSelectorVisible(selector: String) = page.`$`(selector).await() != null

    override suspend fun waitForSelector(
        selector: String,
        domVisible: Boolean,
        hidden: Boolean,
        timeout: Duration,
    ): Boolean {
        return try {
            page.waitForSelector(selector, jsObject<WaitForSelectorOptions>().apply {
                visible = domVisible
                this.hidden = hidden
                this.timeout = timeout.toInt(MILLISECONDS)
            }).await()
            true
        } catch (e: Throwable) {
            false
        }
    }
}

class PuppeteerBrowser(private val browser: puppeteer.Browser) : Browser {
    override suspend fun openedPages() =
        browser.pages().await().toList().map { PuppeteerPage(it) }

    override suspend fun newPage() =
        PuppeteerPage(browser.newPage().await())

    override suspend fun close() =
        browser.close().await()
}
