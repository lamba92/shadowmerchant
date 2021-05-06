package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.Serializable

@Serializable
data class BuyableItem(
    val url: String,
    val maxPrice: Double,
    val tags: List<String> = emptyList(),
    val customNameSelector: String? = null,
    val customAddToCartFlow: ClickFlow? = null,
    val customBuyoutFlow: ClickFlow? = null,
    val customPriceSelector: String? = null
)
