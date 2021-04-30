package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Store {
    abstract val name: String
    abstract val loginData: LoginData
    abstract val checkoutData: CheckoutData
    abstract val cartLink: String
    abstract val buyCartFlow: String

    @Serializable
    @SerialName("amazon")
    data class Amazon(
        override val loginData: LoginData,
        override val checkoutData: CheckoutData,
        override val cartLink: String,
        override val buyCartFlow: String,
        val country: String,
    ) : Store() {
        override val name: String
            get() = "Amazon $country"
    }

    @Serializable
    data class CheckoutData(
        val flow: ClickFlow,
        val orderCompletedVerifier: SelectorTextVerifier
    )
}
