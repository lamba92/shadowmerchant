package com.github.lamba92.shadowmerchant

import NodeJS.get
import process
import path.path

object ChromeData {
    private val defaultWindowsLocation
        get() = path.resolve(process.env["LOCALAPPDATA"]!!, "Google", "Chrome", "User Data")

    private val defaultMacOsLocation
        get() = path.resolve("~", "Library", "Application Support", "Google", "Chrome")

    private val defaultLinuxLocation
        get() = path.resolve("~", ".config", "google-chrome")

    val defaultUserDataDirectory
        get() = when (process.platform) {
            "win32" -> defaultWindowsLocation
            "darwin" -> defaultMacOsLocation
            "linux" -> defaultLinuxLocation
            else -> error("Current OS not supported")
        }
}
