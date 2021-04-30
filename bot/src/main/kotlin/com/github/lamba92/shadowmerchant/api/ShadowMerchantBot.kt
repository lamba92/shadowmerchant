package com.github.lamba92.shadowmerchant.api

import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.data.Store

class ShadowMerchantBot private constructor(private val browser: Browser) {

    companion object {
        suspend fun create(
            headless: Boolean = false,
            viewPort: ViewPortSize? = null
        ) = ShadowMerchantBot(Browser.launch(headless, viewPort))

        suspend operator fun invoke(
            headless: Boolean = false,
            viewPort: ViewPortSize? = null
        ) = create(headless, viewPort)
    }

    suspend fun buyItems(items: List<BuyableItem>) {
        val stores = items.map { it.store }
    }

    private suspend fun loginStore(store: Store) {

    }
}
