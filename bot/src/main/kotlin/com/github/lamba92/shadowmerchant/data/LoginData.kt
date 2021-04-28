package com.github.lamba92.shadowmerchant.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LoginData {
    abstract val username: String
    abstract val password: String

    abstract val usernameInputSelector: String
    abstract val passwordInputSelector: String

    abstract val loginLink: String
    abstract val stayConnectedCheckboxSelector: String?

    @Serializable
    @SerialName("singleStep")
    data class SingleStepLogin(
        override val username: String,
        override val password: String,
        override val loginLink: String,
        override val usernameInputSelector: String,
        override val passwordInputSelector: String,
        override val stayConnectedCheckboxSelector: String? = null,
        val loginSelector: String
    ) : LoginData()

    @Serializable
    @SerialName("doubleStep")
    data class DoubleStepLogin(
        override val username: String,
        override val usernameInputSelector: String,
        val firstPageSelector: String,
        override val password: String,
        override val passwordInputSelector: String,
        val secondPageSelector: String,
        override val stayConnectedCheckboxSelector: String? = null,
        override val loginLink: String
    ) : LoginData()
}
