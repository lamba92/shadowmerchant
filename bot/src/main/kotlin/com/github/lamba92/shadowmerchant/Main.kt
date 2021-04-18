package com.github.lamba92.shadowmerchant

import kotlinx.coroutines.await
import puppeteer.LaunchOptions
import puppeteer.launch


suspend fun main() {
    launch(js("{}").unsafeCast<LaunchOptions>().apply { headless = false }).await()
}
