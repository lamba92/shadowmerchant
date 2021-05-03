package com.github.lamba92.shadowmerchant

import Buffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
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

fun <R> CoroutineScope.launchLoop(action: suspend CoroutineScope.() -> R) =
    launch {
        while (isActive) {
            action()
        }
    }

suspend fun Semaphore.awaitWithoutAcquire() {
    acquire()
    release()
}

suspend fun Mutex.awaitWithoutLock() {
    lock()
    unlock()
}
