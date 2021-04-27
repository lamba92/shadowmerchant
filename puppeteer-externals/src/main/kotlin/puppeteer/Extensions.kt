package puppeteer

import kotlinx.coroutines.await

fun Page.onConsole(action: (ConsoleMessage) -> Unit) =
    on("close") { action(it.unsafeCast<ConsoleMessage>()) }

fun Page.onDialog(action: (Dialog) -> Unit) =
    on("dialog") { action(it.unsafeCast<Dialog>()) }

fun Page.onDomContentLoaded(action: () -> Unit) =
    on("domcontentloaded") { action() }

fun Page.onError(action: (Throwable) -> Unit) =
    on("close") { action(it.unsafeCast<Throwable>()) }

fun Page.onFrameAttached(action: (Frame) -> Unit) =
    on("frameattached") { action(it.unsafeCast<Frame>())}

fun Page.onFrameDeatched(action: (Frame) -> Unit) =
    on("framedeatched") { action(it.unsafeCast<Frame>())}

fun Page.onFrameNavigated(action: (Frame) -> Unit) =
    on("framenavigated") {action(it.unsafeCast<Frame>())}

fun Page.onLoad(action: () -> Unit) =
    on("load") { action() }

fun Page.onMetrics(action: (MetricsEventMessage) -> Unit) =
    on("metrics") { action(it.unsafeCast<MetricsEventMessage>()) }

fun Page.onPopup(action: (Page) -> Unit) =
    on("popup") { action(it.unsafeCast<Page>())}

fun Page.onPageError(action: (Throwable) -> Unit) =
    on("pageerror") { action(it.unsafeCast<Throwable>())}

fun Page.onClose(action: () -> Unit) =
    on("close") { action() }

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
