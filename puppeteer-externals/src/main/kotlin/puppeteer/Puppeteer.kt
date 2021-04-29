package puppeteer

import kotlin.js.Promise

@JsModule("puppeteer")
@JsNonModule
external object Puppeteer {
    fun launch(options: LaunchOptions = definedExternally): Promise<Browser>

    /**
     * This methods attaches Puppeteer to an existing browser instance.
     */
    fun connect(options: ConnectOptions): Promise<Browser>

    /**
     * Clears all registered handlers.
     */
    fun clearCustomQueryHandlers(): Unit

    fun registerCustomQueryHandler(name: String, queryHandler: CustomQueryHandler): String

    val networkConditions: Map<String, PredefinedNetworkConditions>

    val product: String

}
