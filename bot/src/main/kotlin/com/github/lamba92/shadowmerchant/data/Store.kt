package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        val country: String,
    ) : Store() {
        override val name: String = "Amazon $country"
    }

    @Serializable
    data class CheckoutData(
        val flow: ClickFlow,
        val orderCompletedVerifier: SelectorTextVerifier
    )
}
