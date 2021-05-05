package puppeteer

import NodeJS.EventEmitter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@Suppress("FunctionName")
internal fun Object_values(jsObject: dynamic) =
    js("Object.entries").unsafeCast<(dynamic) -> Array<Array<Any>>>()(jsObject)

internal fun <T> entriesOf(jsObject: dynamic): List<Pair<String, T>> {
    return Object_values(jsObject).map { (key, value) -> key as String to value.unsafeCast<T>() }
        .filter { it.second != null }
}

fun <T> jsObject() =
    js("{}").unsafeCast<T>()

internal fun <T> EventEmitter.flowFor(eventName: String) =
    callbackFlow {
        val callback: (dynamic) -> Unit = { trySend(it.unsafeCast<T>()).isSuccess }
        on(eventName, callback)
        awaitClose { off(eventName, callback) }
    }
