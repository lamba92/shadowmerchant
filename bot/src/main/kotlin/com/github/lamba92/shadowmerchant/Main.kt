package com.github.lamba92.shadowmerchant

import NodeJS.get
import kotlinx.coroutines.await
import puppeteer.Puppeteer
import puppeteer.launch
import process
import path.path

suspend fun main() {
    val chrome = Puppeteer.launch {
        headless = false
        userDataDir = ChromeUserData.defaultLocation
    }
    chrome.newPage().await()
}

object ChromeUserData {
    val defaultWindowsLocation
        get() = path.resolve(process.env["LOCALAPPDATA"]!!, "Google", "Chrome", "User Data")

    val defaultMacOsLocation
        get() = path.resolve("~", "Library", "Application Support", "Google", "Chrome")

    val defaultLinuxLocation
        get() = path.resolve("~", ".config", "google-chrome")

    val defaultLocation
        get() = when (process.platform) {
            "win32" -> defaultWindowsLocation
            "darwin" -> defaultMacOsLocation
            "linux" -> defaultLinuxLocation
            else -> error("Current OS not supported")
        }
}
