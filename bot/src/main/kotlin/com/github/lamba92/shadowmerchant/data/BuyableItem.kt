package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.Serializable

@Serializable
data class BuyableItem(
    val link: String,
    val addToCartFlow: ClickFlow,
    val buyoutFlow: ClickFlow?,
    val store: Store,
    val maxPrice: Double
)
