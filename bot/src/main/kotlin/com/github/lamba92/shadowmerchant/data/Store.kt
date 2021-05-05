package com.github.lamba92.shadowmerchant.data

import com.github.lamba92.shadowmerchant.api.Page
import com.github.lamba92.shadowmerchant.api.navigateIfNotAlready
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

val specializedProcessors: Map<KClass<out Store>, StoreProcessor> = mapOf(
    Amazon::class to AmazonStoreProcessor
)

fun process(store: Store) {
    val processor: StoreProcessor = specializedProcessors[store::class] ?: BaseStoreProcessor()
    //processor...
}

interface StoreProcessor {

    fun supports(store: Store): Boolean
}

class BaseStoreProcessor() : StoreProcessor {

    override fun supports(store: Store): Boolean = true
}

object AmazonStoreProcessor : StoreProcessor {

    override fun supports(store: Store): Boolean = store is Amazon
}

@Serializable
sealed class Store {
    abstract val name: String
    abstract val loginData: LoginData
    abstract val checkoutData: CheckoutData
    abstract val addToCartFlow: ClickFlow
    abstract val cartLink: String
    abstract val buyCartFlow: ClickFlow
    abstract val buyoutFlow: ClickFlow
    abstract val buyableItems: Set<BuyableItem>
    abstract val priceSelector: String

    /**
     * Tries to add the item to cart. If successful, the provided [page]
     * will be in the Amazon.it cart url. Page should be in the cart link afterwards.
     * @param item the item to add to the cart.
     * @param page the page to use.
     * @return `true` if item has been added to the cart.
     */
    open suspend fun addItemToCart(item: BuyableItem, page: Page): Boolean {
        page.navigateIfNotAlready(item.url)
        val flow = item.customAddToCartFlow ?: addToCartFlow
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
    open suspend fun checkAvailability(item: BuyableItem, page: Page): Boolean {
        page.navigateIfNotAlready(item.url)
        val selectors = buildList {
            item.customAddToCartFlow?.let { add(it.selectors.first().selector) }
            item.customBuyoutFlow?.let { add(it.selectors.first().selector) }
            add(addToCartFlow.selectors.first().selector)
            add(buyoutFlow.selectors.first().selector)
        }
        return selectors.any { page.isSelectorVisible(it) }
    }

    open suspend fun checkPrice(item: BuyableItem, page: Page): Boolean {
        page.navigateIfNotAlready(item.url)
        return page.innerText(item.customPriceSelector ?: priceSelector)
            ?.let { priceRegex.matchEntire(it) }
            ?.value
            ?.replace(".", "")
            ?.replace(",", ".")
            ?.toDoubleOrNull()
            ?.let { it <= item.maxPrice }
            ?: false
    }

    open suspend fun loginStore(store: Store, page: Page) {
        page.navigateIfNotAlready(store.loginData.loginLink)
        page.type(store.loginData.usernameInputSelector, store.loginData.username)
        val loginData = store.loginData
        if (loginData is LoginData.DoubleStepLogin)
            page.click(loginData.firstStepButtonSelector, true)
        page.type(loginData.passwordInputSelector, store.loginData.password)
        loginData.stayConnectedCheckboxSelector?.let { page.click(it) }
        page.click(loginData.loginButtonSelector, true)
    }

    companion object {
        val priceRegex: Regex by lazy { Regex("[\\d]+([.,]\\d+)?") }
    }
}

@Serializable
@SerialName("amazon")
data class Amazon(
    override val loginData: LoginData,
    override val checkoutData: CheckoutData,
    override val addToCartFlow: ClickFlow,
    override val cartLink: String,
    override val buyCartFlow: ClickFlow,
    override val buyoutFlow: ClickFlow,
    override val buyableItems: Set<BuyableItem>,
    override val priceSelector: String,
    val country: String,
) : Store() {
    override val name: String = "Amazon $country"

    companion object {
        fun italy(email: String, password: String, items: Set<BuyableItem>) = Amazon(
            loginData = LoginData.DoubleStepLogin(
                username = email,
                usernameInputSelector = "#ap_email",
                firstStepButtonSelector = "#continue",
                password = password,
                passwordInputSelector = "#ap_password",
                loginButtonSelector = "#signInSubmit",
                stayConnectedCheckboxSelector = "#authportal-main-section > div:nth-child(2) > div > div > div > form > div > div:nth-child(7) > div > div > label > div > label > input[type=checkbox]",
                loginLink = "https://www.amazon.it/ap/signin?openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.it%2Fref%3Dnav_signin&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=itflex&openid.mode=checkid_setup&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&"
            ),
            checkoutData = CheckoutData(
                flow = ClickFlow(
                    selectors = listOf(
                        ClickFlow.SelectorWithWaitTime(
                            selector = "#submitOrderButtonId > span > input"
                        )
                    ),
                    canSkipSelector = false
                ),
                orderCompletedVerifier = SelectorTextVerifier(
                    selector = "#widget-purchaseConfirmationStatus > div > h4",
                    text = "Grazie, il tuo ordine è stato ricevuto."
                )
            ),
            addToCartFlow = ClickFlow(
                selectors = listOf(
                    ClickFlow.SelectorWithWaitTime(
                        selector = "#add-to-cart-button"
                    )
                ),
                canSkipSelector = false
            ),
            cartLink = "https://www.amazon.it/gp/cart/view.html",
            buyCartFlow = ClickFlow(
                selectors = listOf(
                    ClickFlow.SelectorWithWaitTime(
                        selector = "#sc-buy-box-ptc-button > span > input"
                    )
                ),
                canSkipSelector = false
            ),
            buyoutFlow = ClickFlow(
                selectors = listOf(
                    ClickFlow.SelectorWithWaitTime(
                        selector = "#buy-now-button"
                    )
                ),
                canSkipSelector = false
            ),
            buyableItems = items,
            priceSelector = "#priceblock_ourprice",
            country = "ITA"
        )
    }

}

@Serializable
data class CheckoutData(
    val flow: ClickFlow,
    val orderCompletedVerifier: SelectorTextVerifier,
)
