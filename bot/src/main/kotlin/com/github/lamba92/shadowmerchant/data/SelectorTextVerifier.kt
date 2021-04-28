package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.Serializable

@Serializable
data class SelectorTextVerifier(
    val selector: String,
    val text: String
)
