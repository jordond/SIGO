package now.shouldigooutside.core.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

public actual val appIdentifier: String
    get() = "now.shouldigooutside.ios"

public actual fun launchAppStore() {
    val url = "itms-apps://itunes.apple.com/app/id$appIdentifier"
    UIApplication.sharedApplication.openURL(NSURL(string = url))
}
