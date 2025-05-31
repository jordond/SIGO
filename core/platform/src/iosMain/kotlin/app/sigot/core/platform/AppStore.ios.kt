package app.sigot.core.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

public actual val appIdentifier: String
    get() = "app.sigot.ios"

public actual fun launchAppStore() {
    val url = "itms-apps://itunes.apple.com/app/id$appIdentifier"
    UIApplication.sharedApplication.openURL(NSURL(string = url))
}
