package app.sigot.core.platform

import co.touchlab.kermit.Logger

public actual val appIdentifier: String = ""

public actual fun launchAppStore() {
    Logger.i { "Not supported on this platform" }
}
