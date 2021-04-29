package puppeteer

import kotlinx.coroutines.await

val Page.onConsoleFlow
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

val HTTPResponse.headersMap
    get() = entriesOf<String>(headers()).toMap()

suspend fun Page.navigate(url: String, options: GotoOptions.() -> Unit) =
    goto(url, jsObject<GotoOptions>().apply(options)).await()

suspend fun Page.navigate(url: String) =
    goto(url).await()

suspend fun Page.type(selector: String, text: String, delay: Int = 0) =
    type(selector, text, jsObject<TypeOptions>().also { it.delay = delay }).await()
