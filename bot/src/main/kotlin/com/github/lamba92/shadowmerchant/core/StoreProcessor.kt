package com.github.lamba92.shadowmerchant.core

import com.github.lamba92.shadowmerchant.api.Page
import com.github.lamba92.shadowmerchant.data.BuyableItem
import kotlin.time.Duration

interface StoreProcessor {

    fun supports(store: Store): Boolean
    suspend fun loginStore(store: Store, page: Page)
    suspend fun checkAvailability(store: Store, item: BuyableItem, page: Page): Boolean
    suspend fun checkPrice(store: Store, item: BuyableItem, page: Page): Boolean
    suspend fun buyItem(store: Store, page: Page, retryAttempt: Int=10, retryDelay: Duration= Duration.Companion.seconds(1)):Boolean

}
