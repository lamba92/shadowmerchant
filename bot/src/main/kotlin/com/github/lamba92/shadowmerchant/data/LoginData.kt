package com.github.lamba92.shadowmerchant.data

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
    data class DoubleStepLogin(
        override val username: String,
        override val password: String,
        override val loginLink: String,
        override val usernameInputSelector: String,
        override val passwordInputSelector: String,
        override val stayConnectedCheckboxSelector: String? = null,
        val firstPageSelector: String,
        val secondPageSelector: String
    ) : LoginData()
}
