@file:JsModule("puppeteer-core")
@file:JsNonModule

package puppeteer

external fun launch(options: LaunchOptions): Browser

external val networkConditions: Map<String, PredefinedNetworkConditions>

external val product: String

external fun registerCustomQueryHandler(name: String, )

external interface CustomQueryHandler {
    fun queryOne()
    fun queryAll()
}

external interface PredefinedNetworkConditions {
    val download: Double
    val upload: Double
    val latency: Int
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

external interface Browser {

}
