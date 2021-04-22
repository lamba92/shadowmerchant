package com.github.lamba92.shadowmerchant

import NodeJS.Process
import NodeJS.get
import kotlinx.coroutines.await
import path.path.PlatformPath
import puppeteer.Puppeteer
import puppeteer.launch

suspend fun main() {
    val chrome = Puppeteer.launch { headless = false }.await()

    chrome.newPage().await()
}
