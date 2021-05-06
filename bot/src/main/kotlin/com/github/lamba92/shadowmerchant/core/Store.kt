package com.github.lamba92.shadowmerchant.core


import com.github.lamba92.shadowmerchant.core.processors.AmazonStoreProcessor
import com.github.lamba92.shadowmerchant.core.stores.Amazon
import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.data.ClickFlow
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.SelectorTextVerifier
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

val specializedProcessors: Map<KClass<out Store>, StoreProcessor> = mapOf(
    Amazon::class to AmazonStoreProcessor
)

//fun process(store: Store) {
//    val processor: StoreProcessor = specializedProcessors[store::class] ?: BaseStoreProcessor()
//    //processor...
//}
//
//class BaseStoreProcessor() : StoreProcessor {
//
//    override fun supports(store: StoreProcessor.Store): Boolean = true
//}

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

    companion object {
        val priceRegex: Regex by lazy { Regex("[\\d]+([.,]\\d+)?") }
    }
}

@Serializable
data class CheckoutData(
    val flow: ClickFlow,
    val orderCompletedVerifier: SelectorTextVerifier,
)
