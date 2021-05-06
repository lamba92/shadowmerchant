package com.github.lamba92.shadowmerchant.core.processors

import com.github.lamba92.shadowmerchant.Logger
import com.github.lamba92.shadowmerchant.api.Page
import com.github.lamba92.shadowmerchant.api.navigateIfNotAlready
import com.github.lamba92.shadowmerchant.core.Store
import com.github.lamba92.shadowmerchant.core.StoreProcessor
import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.data.LoginData
import kotlinx.coroutines.delay
import kotlin.time.Duration

open class BaseStoreProcessor : StoreProcessor {

    protected val logger by lazy { Logger("BaseStoreProcessor") }

    /**
     * Tries to add the item to cart. If successful, the provided [page]
     * will be in the Amazon.it cart url. Page should be in the cart link afterwards.
     * @param item the item to add to the cart.
     * @param page the page to use.
     * @return `true` if item has been added to the cart.
     */
    suspend fun Store.addItemToCart(page: Page, item: BuyableItem): Boolean {
        val itemName = buildString {
            page.innerText(itemNameSelector)?.let { append(it) }
                ?: item.customNameSelector?.let { page.innerText(it) }?.let { append("$it ") }
            append(item.url)
        }
        logger.info("Adding $itemName to cart.")
        page.navigateIfNotAlready(item.url)
        val flow = item.customAddToCartFlow ?: addToCartFlow
        logger.debug("Flow for $itemName is $flow")
        for ((index, selectorWithWaitTime) in flow.selectors.withIndex()) {
            val (selector, waitTime) = selectorWithWaitTime
            if (!page.waitForSelector(selector))
                return false
            page.click(selector, waitForNavigation = true)
            if (flow.selectors.lastIndex != index)
                delay(waitTime)
        }
        logger.warn("$itemName added to cart.")
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
    override suspend fun Store.checkAvailability(page: Page, item: BuyableItem): Boolean {
        page.navigateIfNotAlready(item.url)
        val selectors = buildList {
            item.customAddToCartFlow?.let { add(it.selectors.first().selector) }
            item.customBuyoutFlow?.let { add(it.selectors.first().selector) }
            add(addToCartFlow.selectors.first().selector)
            add(buyoutFlow.selectors.first().selector)
        }
        return selectors.any { page.isSelectorVisible(it) }
    }

    override suspend fun Store.checkPrice(page: Page, item: BuyableItem): Boolean {
        page.navigateIfNotAlready(item.url)
        return page.innerText(item.customPriceSelector ?: priceSelector)
            ?.let { Store.priceRegex.matchEntire(it) }
            ?.value
            ?.replace(".", "")
            ?.replace(",", ".")
            ?.toDoubleOrNull()
            ?.let { it <= item.maxPrice }
            ?: false
    }

    override suspend fun Store.buyItem(page: Page, retryAttempt: Int, retryDelay: Duration): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun Store.login(page: Page) {
        page.navigateIfNotAlready(loginData.loginLink)
        page.type(loginData.usernameInputSelector, loginData.username)
        val loginData = loginData
        if (loginData is LoginData.DoubleStepLogin)
            page.click(loginData.firstStepButtonSelector, true)
        page.type(loginData.passwordInputSelector, loginData.password)
        loginData.stayConnectedCheckboxSelector?.let { page.click(it) }
        page.click(loginData.loginButtonSelector, true)
    }

}

object AmazonStoreProcessor : BaseStoreProcessor()
