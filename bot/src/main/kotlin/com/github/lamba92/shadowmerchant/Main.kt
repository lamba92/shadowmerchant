package com.github.lamba92.shadowmerchant

import NodeJS.get
import com.github.lamba92.shadowmerchant.api.Browser
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.Store
import kotlinx.serialization.json.Json
import path.path
import process

suspend fun main() {
    val chrome = Browser.launch(false, null)
    val file = path.resolve(process.env["STORES_DIR"]!!, "amazon_it.json")
    val store = Json.decodeFromString(Store.serializer(), readFile(file).toString("utf8"))
    with(chrome.openedPages().first()) {
        navigateTo(store.loginData.loginLink)
        val loginData = store.loginData
        when (loginData) {
            is LoginData.SingleStepLogin -> TODO("not yet implemented")
            is LoginData.DoubleStepLogin -> {
                type(loginData.usernameInputSelector, store.loginData.username)
                click(loginData.firstPageSelector, true)
                type(loginData.passwordInputSelector, store.loginData.password)
                loginData.stayConnectedCheckboxSelector?.let { click(it) }
                click(loginData.secondPageSelector, true)
            }
        }
        navigateTo(store.cartLink)
        click(store.buyCartFlow, true)
        store.checkoutData.flow.selectors.forEach {
            click(it.selector, true)
        }
    }
}
