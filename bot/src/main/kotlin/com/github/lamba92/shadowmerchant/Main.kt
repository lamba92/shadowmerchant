package com.github.lamba92.shadowmerchant

import Buffer
import NodeJS.get
import com.github.lamba92.shadowmerchant.api.Browser
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.Store
import fs.readFile
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import path.path
import process
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.toDuration

suspend fun main() {
    val chrome = Browser.launch(false, null)
    val file = path.resolve(process.env["STORES_DIR"]!!, "amazon_it.json")
    val store = Json.decodeFromString(Store.serializer(), readFile(file).toString("utf8"))
    val page = chrome.openedPages().first()
    delay(1.toDuration(SECONDS))
    page.navigateTo(store.loginData.loginLink)
    val loginData = store.loginData
    when (loginData) {
        is LoginData.SingleStepLogin -> TODO("not yet implemented")
        is LoginData.DoubleStepLogin -> {
            page.type(loginData.usernameInputSelector, store.loginData.username, 5.toDuration(MILLISECONDS))
            delay(0.5.toDuration(SECONDS))
            page.click(loginData.firstPageSelector)
            page.waitForNavigation()
            page.type(loginData.passwordInputSelector, store.loginData.password, 5.toDuration(MILLISECONDS))
            loginData.stayConnectedCheckboxSelector?.let { page.click(it) }
            delay(0.5.toDuration(SECONDS))
            page.click(loginData.secondPageSelector)
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
