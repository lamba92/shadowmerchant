package com.github.lamba92.shadowmerchant

import Buffer
import com.github.lamba92.shadowmerchant.data.ClickFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.NANOSECONDS
import kotlin.time.toDuration

suspend fun readFile(path: String) = suspendCoroutine<Buffer> { continuation ->
    fs.readFile(path) { err: Error?, data: Buffer? ->
        when {
            err != null -> continuation.resumeWithException(err)
            data != null -> continuation.resume(data)
            else -> continuation.resumeWithException(IllegalStateException("Both error and data is null."))
        }
    }
}

object DurationSerializer : KSerializer<Duration> {
    override val descriptor = PrimitiveSerialDescriptor("Duration", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder) = decoder.decodeLong().toDuration(NANOSECONDS)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeLong(value.toLong(NANOSECONDS))
    }
}
