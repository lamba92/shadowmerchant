package com.github.lamba92.shadowmerchant.core.processors

import com.github.lamba92.shadowmerchant.api.Page
import com.github.lamba92.shadowmerchant.api.navigateIfNotAlready
import com.github.lamba92.shadowmerchant.core.Store
import com.github.lamba92.shadowmerchant.core.StoreProcessor
import com.github.lamba92.shadowmerchant.core.stores.Amazon
import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.data.LoginData
import kotlinx.coroutines.delay
import kotlin.time.Duration

object AmazonStoreProcessor : StoreProcessor {
    override fun supports(store: Store): Boolean = store is Amazon
    /**
     * Tries to add the item to cart. If successful, the provided [page]
     * will be in the Amazon.it cart url. Page should be in the cart link afterwards.
     * @param item the item to add to the cart.
     * @param page the page to use.
     * @return `true` if item has been added to the cart.
     */

    suspend fun addItemToCart(store: Store, item: BuyableItem, page: Page): Boolean {
        page.navigateIfNotAlready(item.url)
        val flow = item.customAddToCartFlow ?: store.addToCartFlow
        for ((index, selectorWithWaitTime) in flow.selectors.withIndex()) {
            val (selector, waitTime) = selectorWithWaitTime
            if (!page.waitForSelector(selector))
                return false
            page.click(selector, waitForNavigation = true)
            if (flow.selectors.lastIndex != index)
                delay(waitTime)
        }
        return true
    }

    /**
     * Checks if the [item] is available using the given [page] using. The function checks if
     * the cart flow or the buyout flow can be started by checking their respective first
     * selectors visibility.
     * @param item the item to check.
     * @param page the page to use.
     * @return `true` if the price in the [page] is lower than [BuyableItem.maxPrice].
     */
    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun checkAvailability(store: Store, item: BuyableItem, page: Page): Boolean {
        page.navigateIfNotAlready(item.url)
        val selectors = buildList {
            item.customAddToCartFlow?.let { add(it.selectors.first().selector) }
            item.customBuyoutFlow?.let { add(it.selectors.first().selector) }
            add(store.addToCartFlow.selectors.first().selector)
            add(store.buyoutFlow.selectors.first().selector)
        }
        return selectors.any { page.isSelectorVisible(it) }
    }

    override suspend fun checkPrice(store: Store, item: BuyableItem, page: Page): Boolean {
        page.navigateIfNotAlready(item.url)
        return page.innerText(item.customPriceSelector ?: store.priceSelector)
            ?.let { Store.priceRegex.matchEntire(it) }
            ?.value
            ?.replace(".", "")
            ?.replace(",", ".")
            ?.toDoubleOrNull()
            ?.let { it <= item.maxPrice }
            ?: false
    }

    override suspend fun buyItem(store: Store, page: Page, retryAttempt: Int, retryDelay: Duration): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun loginStore(store: Store, page: Page) {
        page.navigateIfNotAlready(store.loginData.loginLink)
        page.type(store.loginData.usernameInputSelector, store.loginData.username)
        val loginData = store.loginData
        if (loginData is LoginData.DoubleStepLogin)
            page.click(loginData.firstStepButtonSelector, true)
        page.type(loginData.passwordInputSelector, store.loginData.password)
        loginData.stayConnectedCheckboxSelector?.let { page.click(it) }
        page.click(loginData.loginButtonSelector, true)
    }


}
