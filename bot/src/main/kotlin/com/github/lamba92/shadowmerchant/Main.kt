package com.github.lamba92.shadowmerchant

import Buffer
import NodeJS.get
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.Store
import fs.readFile
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import path.path
import process
import puppeteer.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.DurationUnit
import kotlin.time.toDuration

suspend fun main() {
    val chrome = Puppeteer.launch {
        headless = false
        defaultViewport = null
    }
    val file = path.resolve(process.env["STORES_DIR"]!!, "amazon_it.json")
    val store = Json.decodeFromString(Store.serializer(), readFile(file).toString("utf8"))
    val page: Page = chrome.pages().await().first()
    delay(2.toDuration(DurationUnit.SECONDS))
    page.navigate(store.loginData.loginLink)
    val loginData = store.loginData
    when (loginData) {
        is LoginData.SingleStepLogin -> TODO("not yet implemented")
        is LoginData.DoubleStepLogin -> {
            page.type(loginData.usernameInputSelector, store.loginData.username, 20)
            delay(1.toDuration(DurationUnit.SECONDS))
            page.click(loginData.firstPageSelector).await()
            page.waitForNavigation().await()
            page.type(loginData.passwordInputSelector, store.loginData.password, 20)
            loginData.stayConnectedCheckboxSelector?.let { page.click(it).await() }
            delay(1.toDuration(DurationUnit.SECONDS))
            page.click(loginData.secondPageSelector).await()
        }
    }

}

suspend fun readFile(path: String) = suspendCoroutine<Buffer> { continuation ->
    readFile(path) { err: Error?, data: Buffer? ->
        when {
            err != null -> continuation.resumeWithException(err)
            data != null -> continuation.resume(data)
            else -> continuation.resumeWithException(IllegalStateException("Both error and data is null."))
        }
    }
}
