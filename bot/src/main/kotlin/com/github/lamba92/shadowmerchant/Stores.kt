package com.github.lamba92.shadowmerchant

import com.github.lamba92.shadowmerchant.data.ClickFlow
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.SelectorTextVerifier
import com.github.lamba92.shadowmerchant.data.Store

object Stores {
    val AMAZON_IT = Store.Amazon(
        loginData = LoginData.DoubleStepLogin(
            "a",
            "#ap_email",
            "#continue",
            "b",
            "#ap_password",
            "#signInSubmit",
            "#authportal-main-section > div:nth-child(2) > div > div > div > form > div > div:nth-child(7) > div > div > label > div > label > input[type=checkbox]",
            "https://www.amazon.it/ap/signin?openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.it%2Fref%3Dnav_signin&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=itflex&openid.mode=checkid_setup&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&"
        ),
        checkoutData = Store.CheckoutData(
            flow = ClickFlow(
                listOf("#submitOrderButtonId > span > input"),
                false
            ),
            orderCompletedVerifier = SelectorTextVerifier(
                selector = "#widget-purchaseConfirmationStatus > div > h4",
                text = "Grazie, il tuo ordine è stato ricevuto."
            )
        ),
        "https://www.amazon.it/gp/cart/view.html",
        "#sc-buy-box-ptc-button > span > input",
        "IT"
    )
}
