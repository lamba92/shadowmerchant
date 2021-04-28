package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.Serializable

@Serializable
sealed class Store {
    abstract val loginData: LoginData
    abstract val checkoutFlow: CheckoutData
    abstract val cartLink: String
    abstract val buyCartSelector: String

    @Serializable
    data class Amazon(
        override val loginData: LoginData,
        override val checkoutFlow: CheckoutData,
        override val cartLink: String,
        override val buyCartSelector: String,
        val country: String,
    ) : Store()

    @Serializable
    data class CheckoutData(
        val checkoutFlow: ClickFlow,
        val orderCompletedVerifier: SelectorTextVerifier
    )
}
