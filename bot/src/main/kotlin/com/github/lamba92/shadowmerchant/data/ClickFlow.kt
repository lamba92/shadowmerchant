package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.Serializable

@Serializable
data class ClickFlow(
    val selectors: List<String>,
    val canSkipSelector: Boolean
)
