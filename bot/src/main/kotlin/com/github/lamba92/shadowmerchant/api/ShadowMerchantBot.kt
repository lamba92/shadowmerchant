package com.github.lamba92.shadowmerchant.api

import com.github.lamba92.shadowmerchant.Logger
import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.Store
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShadowMerchantBot(private val browser: Browser) {

    private val logger = Logger("ShadowMerchantBot")

    companion object {
        suspend fun create(
            headless: Boolean = false,
            viewPort: ViewPortSize? = null,
        ) = ShadowMerchantBot(Browser.launch(headless, viewPort))

        suspend operator fun invoke(
            headless: Boolean = false,
            viewPort: ViewPortSize? = null,
        ) = create(headless, viewPort)
    }

    suspend fun buyItemsFrom(stores: Set<Store>) = coroutineScope {
        logger.info("${stores.size} stores: ${stores.joinToString { it.name }}")
        loginStores(stores)
        val tasks = stores.flatMap { store -> store.buyableItems.map { BuyingTask(store, it) } }
        val pages = browser.newPages(tasks.size.coerceAtMost(5))
        tasks.chunked(pages.size).forEach { subTask ->
            subTask.forEachIndexed { index, buyingTask ->
                launch { TODO("Implement refresh and buy") }
            }
        }
    }

    private data class BuyingTask(
        val store: Store,
        val item: BuyableItem,
    )

    enum class PurchaseAttempt {
        SUCCESSFUL, FAILED
    }

    suspend fun loginStores(stores: Collection<Store>, pagesCount: Int = 3) =
        browser.newPageExecutor<Unit>(stores.size.coerceAtMost(pagesCount)).use {
            for (store in stores) {
                it.offerExecution { loginStore(store) }
            }
        }

    private suspend fun Page.loginStore(store: Store) {
        logger.info("Initiating login to ${store.name}")
        navigateTo(store.loginData.loginLink)
        type(store.loginData.usernameInputSelector, store.loginData.username)
        val loginData = store.loginData
        if (loginData is LoginData.DoubleStepLogin)
            click(loginData.firstStepButtonSelector, true)
        type(loginData.passwordInputSelector, store.loginData.password)
        loginData.stayConnectedCheckboxSelector?.let { click(it) }
        click(loginData.loginButtonSelector, true)
        logger.info("Logged into ${store.name}")
    }

    private suspend fun Page.checkAvailability(task: BuyingTask): Boolean {
        val selectors = buildList {
            task.item.customAddToCartFlow?.let { add(it.selectors.first().selector) }
            task.item.customBuyoutFlow?.let { add(it.selectors.first().selector) }
            add(task.store.addToCartFlow.selectors.first().selector)
            add(task.store.buyoutFlow.selectors.first().selector)
        }
        return selectors.any { isSelectorVisible(it) }
    }

    private suspend fun Page.addItemToCart(task: BuyingTask) {
        val flow = task.item.customAddToCartFlow ?: task.store.addToCartFlow
        for ((index, selectorWithWaitTime) in flow.selectors.withIndex()) {
            val (selector, waitTime) = selectorWithWaitTime
            waitForSelector(selector)
            click(selector, flow.selectors.lastIndex == index)
            if (flow.selectors.lastIndex != index)
                delay(waitTime)
        }
    }
}
