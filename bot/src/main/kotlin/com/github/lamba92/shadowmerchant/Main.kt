package com.github.lamba92.shadowmerchant

import fsextra.copy
import kotlinx.coroutines.await
import puppeteer.Puppeteer
import puppeteer.launch
import tmp.dirSuspending

suspend fun main() {
    val tmpDir = dirSuspending()
    copy(ChromeData.defaultUserDataDirectory, tmpDir).await()
    println(tmpDir)
    val chrome = Puppeteer.launch {
        headless = false
        userDataDir = tmpDir
    }
    val page = chrome.newPage().await()
}
