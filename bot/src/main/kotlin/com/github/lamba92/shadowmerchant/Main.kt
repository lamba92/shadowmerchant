package com.github.lamba92.shadowmerchant

import Buffer
import NodeJS.get
import com.github.lamba92.shadowmerchant.data.Store
import fs.readFile
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import path.path
import process
import puppeteer.Puppeteer
import puppeteer.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    val chrome = Puppeteer.launch { headless = false }
    val file = path.resolve(process.env["STORES_DIR"]!!, "amazon_it.json")
    val store = Json.decodeFromString(Store.serializer(), readFileAsUtf8String(file))
    chrome.pages().await().first()
}

suspend fun readFileAsUtf8String(path: String) = suspendCoroutine<String> { continuation ->
    readFile(path) { err: Error?, data: Buffer? ->
        when {
            err != null -> continuation.resumeWithException(err)
            data != null -> continuation.resume(data.toString("utf8"))
            else -> continuation.resumeWithException(IllegalStateException("Both error and data is null."))
        }
    }
}
