package com.github.lamba92.shadowmerchant.data

import com.github.lamba92.shadowmerchant.DurationSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.toDuration

@Serializable
data class ClickFlow(
    val selectors: List<SelectorWithWaitTime>,
    val canSkipSelector: Boolean
) {

    @Serializable
    data class SelectorWithWaitTime(
        val selector: String,
        @Serializable(with = DurationSerializer::class) val waitAfterClick: Duration = 5.toDuration(MILLISECONDS)
    )
}
