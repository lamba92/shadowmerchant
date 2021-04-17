@file:JsModule("puppeteer-core")
@file:JsNonModule

package puppeteer


import child_process.ChildProcess
import kotlin.js.Promise

external fun launch(options: LaunchOptions): Promise<Browser>

/**
 * This methods attaches Puppeteer to an existing browser instance.
 */
external fun connect(options: ConnectOptions): Promise<Browser>

/**
 * Clears all registered handlers.
 */
external fun clearCustomQueryHandlers(): Unit

external val networkConditions: Map<String, PredefinedNetworkConditions>

external val product: String

external fun registerCustomQueryHandler(name: String, )

external interface CustomQueryHandler {
    fun queryOne()
    fun queryAll()
}

external interface PredefinedNetworkConditions {
    var download: Double
    var upload: Double
    var latency: Int
}

external interface ConnectOptions {
    /**
     * a browser websocket endpoint to connect t
     */
    var browserWSEndpoint: String?

    /**
     * a browser url to connect to, in format `http://${host}:${port}`. Use interchangeably with browserWSEndpoint to let Puppeteer fetch it from [metadata endpoint](https://chromedevtools.github.io/devtools-protocol/#how-do-i-access-the-browser-target).
     */
    var browserURL: String?

    /**
     * Whether to ignore HTTPS errors during navigation. Defaults to false.
     */
    var ignoreHTTPSErrors: Boolean

    /**
     * Sets a consistent viewport for each page. Defaults to an 800x600 viewport. null disables the default viewport.
     */
    var defaultViewport: ViewPort?

    /**
     * Slows down Puppeteer operations by the specified amount of milliseconds. Useful so that you can see what is going on.
     */
    var slowMo: Boolean

    /**
     * Experimental Specify a custom transport object for Puppeteer to use.
     */
    var transport: dynamic

    /**
     * Possible values are: chrome, firefox. Defaults to chrome.
     */
    var product: String
}

external interface LaunchOptions {
    var product: String
    var ignoreHTTPSErrors: Boolean
    var headless: Boolean
    var executablePath: String
    var slowMo: Long
    var defaultViewport: ViewPort
    var args: Array<String>
    var ignoreDefaultArgs: Boolean
    var handleSIGINT: Boolean
    var handleSIGTERM: Boolean
    var handleSIGHUP: Boolean
    var timeout: Boolean
    var dumpio: Boolean
    var userDataDir: String
    var env: Map<String, String> // not sure...
    var devtools: Boolean
    var pipe: Boolean
    var extraPrefsFirefox: Map<String, String>
}

external interface ViewPort {
    var width: Int
    var height: Int
    var deviceScaleFactor: Double
    var isMobile: Boolean
    var hasTouch: Boolean
    var isLandscape: Boolean
}

external interface Browser : EventEmitter {
    fun browserContexts(): Array<BrowserContext>
    fun close(): Promise<Unit>
    fun createIncognitoBrowserContext(): Promise<BrowserContext>
    fun defaultBrowserContext(): BrowserContext
    fun disconnect(): Boolean
    fun isConnected(): Boolean
    fun newPage(): Promise<Page>
    fun pages(): Promise<Array<Page>>
    fun process(): ChildProcess?
    fun target(): Target
    fun targets(): Array<Target>
    fun userAgent(): Promise<String>
    fun version(): Promise<String>
    fun waitForTarget()
}

external interface Target {

}

external interface Page {

}

external interface BrowserContext : EventEmitter {

}

external interface EventEmitter {
    fun addListener(event: String, handler: (dynamic) -> Unit): EventEmitter
    fun emit(event: String, eventData: dynamic)

    // incomplete
}