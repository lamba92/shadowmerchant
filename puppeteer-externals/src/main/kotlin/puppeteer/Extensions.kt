package puppeteer

import NodeJS.EventEmitter
import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

val Page.onConsole
    get() = flowFor<ConsoleMessage>("console")

val Page.onDialogFLow
    get() = flowFor<Dialog>("dialog")

val Page.onDomContentLoadedFLow
    get() = flowFor<Unit>("domcontentloaded")

val Page.onErrorFLow
    get() = flowFor<Throwable>("close")

val Page.onFrameAttachedFLow
    get() = flowFor<Frame>("frameattached")

val Page.onFrameDeatchedFLow
    get() = flowFor<Frame>("framedeatched")

val Page.onFrameNavigatedFLow
    get() = flowFor<Frame>("framenavigated")

val Page.onLoadFlow
    get() = flowFor<Unit>("load")

val Page.onMetricsFLow
    get() = flowFor<MetricsEventMessage>("metrics")

val Page.onPopupFLow
    get() = flowFor<Page>("popup")

val Page.onPageErrorFLow
    get() = flowFor<Throwable>("pageerror")

val Page.onCloseFlow
    get() = flowFor<Unit>("close")

val Page.onRequestFlow
    get() = flowFor<HTTPRequest>("request")

val Page.onRequestFailedFlow
    get() = flowFor<HTTPRequest>("requestfailed")

val Page.onRequestFinishedFlow
    get() = flowFor<HTTPRequest>("requestfinished")

val Page.onResponseFlow
    get() = flowFor<HTTPResponse>("response")

val Page.onWorkerCreatedFlow
    get() = flowFor<WebWorker>("workercreated")

val Page.onWorkerDestroyedFlow
    get() = flowFor<WebWorker>("workerdestroyed")

suspend fun Puppeteer.launch(configAction: LaunchOptions.() -> Unit): Browser =
    launch(jsObject<LaunchOptions>().apply(configAction)).await()

val MetricsEventMessage.metricsMap
    get() = entriesOf<Double>(metrics).toMap()

@Suppress("FunctionName")
internal fun Object_values(jsObject: dynamic) =
    js("Object.entries").unsafeCast<(dynamic) -> Array<Array<Any>>>()(jsObject)

internal fun <T> entriesOf(jsObject: dynamic): List<Pair<String, T?>> {
    return Object_values(jsObject).map { (key, value) -> key as String to value.unsafeCast<T>() }
}

internal fun <T> jsObject() =
    js("{}").unsafeCast<T>()

internal fun <T> EventEmitter.flowFor(eventName: String) =
    callbackFlow<T> {
        val callback: (Any) -> Unit = { offer(it.unsafeCast<T>()) }
        on(eventName, callback)
        awaitClose { off(eventName, callback) }
    }
