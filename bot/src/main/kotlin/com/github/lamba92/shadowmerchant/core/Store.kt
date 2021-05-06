package com.github.lamba92.shadowmerchant.core


import com.github.lamba92.shadowmerchant.core.processors.BaseStoreProcessor
import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.data.ClickFlow
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.SelectorTextVerifier
import kotlinx.serialization.Serializable


@Serializable
abstract class Store {
    abstract val name: String
    abstract val loginData: LoginData
    abstract val checkoutData: CheckoutData
    abstract val addToCartFlow: ClickFlow
    abstract val cartLink: String
    abstract val buyCartFlow: ClickFlow
    abstract val buyoutFlow: ClickFlow
    abstract val buyableItems: Set<BuyableItem>
    abstract val priceSelector: String
    abstract val itemNameSelector: String

    companion object {
        val priceRegex: Regex by lazy { Regex("[\\d]+([.,]\\d+)?") }
    }
}

@Serializable
data class CheckoutData(
    val flow: ClickFlow,
    val orderCompletedVerifier: SelectorTextVerifier,
)
