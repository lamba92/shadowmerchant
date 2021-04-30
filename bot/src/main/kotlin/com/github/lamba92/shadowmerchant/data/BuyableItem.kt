package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.Serializable

@Serializable
data class BuyableItem(
    val link: String,
    val maxPrice: Double,
    val customAddToCartFlow: ClickFlow? = null,
    val customBuyoutFlow: ClickFlow? = null
)
