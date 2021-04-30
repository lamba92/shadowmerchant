package com.github.lamba92.shadowmerchant

import com.github.lamba92.shadowmerchant.data.*

object Stores {

    fun amazonItaly(email: String, password: String) = Store.Amazon(
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
        checkoutData = Store.CheckoutData(
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
        buyableItems = setOf(BuyableItems.PORTA_CIALDA_BIALETTI_MOKONA),
        country = "ITA"
    )

}

object BuyableItems {
    val PORTA_CIALDA_BIALETTI_MOKONA = BuyableItem(link = "https://www.amazon.it/dp/B00BEWWYG6", 6.0)
}
