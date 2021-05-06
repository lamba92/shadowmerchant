package com.github.lamba92.shadowmerchant.core.stores

import com.github.lamba92.shadowmerchant.core.CheckoutData
import com.github.lamba92.shadowmerchant.core.Store
import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.data.ClickFlow
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.SelectorTextVerifier
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Amazon(
    override val loginData: LoginData,
    override val checkoutData: CheckoutData,
    override val addToCartFlow: ClickFlow,
    override val cartLink: String,
    override val buyCartFlow: ClickFlow,
    override val buyoutFlow: ClickFlow,
    override val buyableItems: Set<BuyableItem>,
    override val priceSelector: String,
    override val itemNameSelector: String,
    val country: String
) : Store() {
    @Transient
    override val name = "Amazon $country"

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
            itemNameSelector = "#productTitle",
            country = "ITA"
        )
    }
}
