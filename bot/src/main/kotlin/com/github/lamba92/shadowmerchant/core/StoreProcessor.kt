package com.github.lamba92.shadowmerchant.core

import com.github.lamba92.shadowmerchant.api.Page
import com.github.lamba92.shadowmerchant.core.processors.AmazonStoreProcessor
import com.github.lamba92.shadowmerchant.core.processors.BaseStoreProcessor
import com.github.lamba92.shadowmerchant.core.stores.Amazon
import com.github.lamba92.shadowmerchant.data.BuyableItem
import kotlin.reflect.KClass
import kotlin.time.Duration

interface StoreProcessor {

    companion object {

        @InternalShadowMerchantApi
        val processors: MutableMap<KClass<out Store>, StoreProcessor> by lazy {
            mutableMapOf(Amazon::class to AmazonStoreProcessor)
        }

        @OptIn(InternalShadowMerchantApi::class)
        inline fun <reified T: Store> registerStoreProcessor(processor: StoreProcessor) {
            processors[T::class] = processor
        }

        @OptIn(InternalShadowMerchantApi::class)
        inline fun <reified T : Store> getFor(store: T, action: StoreProcessor.() -> Unit): StoreProcessor =
            (processors[store::class] ?: BaseStoreProcessor()).apply(action)

    }

    suspend fun Store.login(page: Page)
    suspend fun Store.checkAvailability(page: Page, item: BuyableItem): Boolean
    suspend fun Store.checkPrice(page: Page, item: BuyableItem): Boolean
    suspend fun Store.buyItem(
        page: Page,
        retryAttempt: Int = 10,
        retryDelay: Duration = Duration.Companion.seconds(1),
    ): Boolean

}

@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR, message = "This is an internal Shadow Merchant API that " +
            "should not be used from outside of Shadow Merchant. No compatibility guarantees are provided. "
)
annotation class InternalShadowMerchantApi
