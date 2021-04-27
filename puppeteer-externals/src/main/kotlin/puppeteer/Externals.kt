@file:JsModule("puppeteer")
@file:JsNonModule

package puppeteer


import NodeJS.EventEmitter
import child_process.ChildProcess
import org.w3c.dom.Element
import kotlin.js.Promise

/**
 * Clears all registered handlers.
 */
external fun clearCustomQueryHandlers(): Unit

external val networkConditions: Map<String, PredefinedNetworkConditions>

external val product: String

external fun registerCustomQueryHandler(name: String, queryHandler: CustomQueryHandler): String

external interface CustomQueryHandler {
    fun queryOne(element: Element, selector: String): Element?
    fun queryAll(element: Element, selector: String): Array<Element>
}

external interface ConsoleMessage {
    // TODO
}

external interface Dialog {
    //TODO
}

external interface Frame {
    //TODO
}

external interface HTTPRequest {
    //TODO
}

external interface HTTPResponse {
    //TODO
}

external interface MetricsEventMessage {
    var title: String
    var metrics: dynamic
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
    var product: String?
    var ignoreHTTPSErrors: Boolean?
    var headless: Boolean?
    var executablePath: String?
    var slowMo: Long?
    var defaultViewport: ViewPort?
    var args: Array<String>?
    var ignoreDefaultArgs: Boolean?
    var handleSIGINT: Boolean?
    var handleSIGTERM: Boolean?
    var handleSIGHUP: Boolean?
    var timeout: Boolean?
    var dumpio: Boolean?
    var userDataDir: String?
    var env: Map<String, String>? // not sure...
    var devtools: Boolean?
    var pipe: Boolean?
    var extraPrefsFirefox: Map<String, String>?
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
    fun waitForTarget(predicate: (Target) -> Boolean, options: TargetOptions): Promise<Target>
    fun wsEndpoint(): String
}

external interface TargetOptions {
    var timeout: Long
}

external interface Target {
    fun browser(): Browser
    fun browserContext(): BrowserContext
    fun createCDPSession(): Promise<CDPSession>
    fun opener(): Promise<Target?>
    fun page(): Promise<Page?>
    fun type(): String
    fun url(): String
    fun worker(): Promise<WebWorker>
}

external interface WebWorker {
    // TODO
}

/**
 * The [CDPSession] instances are used to talk raw Chrome Devtools Protocol:
 *
 * protocol methods can be called with `session.send` method.
 * protocol events can be subscribed to with `session.on` method.
 * Useful links:
 *
 * Documentation on DevTools Protocol can be found here: DevTools Protocol Viewer.
 * Getting Started with DevTools Protocol: https://github.com/aslushnikov/getting-started-with-cdp/blob/master/README.md
 * ```kotlin
 * const client = await page.target().createCDPSession();
 * await client.send('Animation.enable');
 * client.on('Animation.animationCreated', () => console.log('Animation created!'));
 * const response = await client.send('Animation.getPlaybackRate');
 * console.log('playback rate is ' + response.playbackRate);
 * await client.send('Animation.setPlaybackRate', {
 *      playbackRate: response.playbackRate / 2
 * });
 * ```
 */
external interface CDPSession : EventEmitter {
    fun detach(): Promise<Unit>
    fun send(method: (dynamic) -> Unit): Promise<dynamic>
}

external interface Coverage {
    fun startCSSCoverage(options: ResetOnNavigationOptions): Promise<Unit>
}

external interface ResetOnNavigationOptions {
    var resetOnNavigation: Boolean
}

external interface Page : EventEmitter {
    // TODO
}

external interface BrowserContext : EventEmitter {
    fun browser(): Browser
    fun clearPermissionOverrides(): Promise<Unit>
    fun close(): Promise<Unit>
    fun isIncognito(): Boolean
    fun newPage(): Promise<Page>
    fun overridePermissions(origin: String, permissions: Array<String>): Promise<Unit>
    fun pages(): Array<Array<Page>>
    fun targets(): Array<Array<Target>>
    fun waitForTarget(predicate: (Target) -> Unit, options: TargetOptions): Promise<Target>

}
