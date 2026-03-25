package now.shouldigooutside.core.platform

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

public actual val appIdentifier: String
    get() = "now.shouldigooutside.ios"

public actual fun launchAppStore() {
    val url = "itms-apps://itunes.apple.com/app/id6760564555"
    UIApplication.sharedApplication.openURL(NSURL(string = url))
}

public actual fun shareApp() {
    val shareUrl = NSURL(string = "https://apps.apple.com/us/app/should-i-go-outside-weather/id6760564555")
    val activityVC = UIActivityViewController(
        activityItems = listOf(shareUrl),
        applicationActivities = null,
    )
    val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootVC?.presentViewController(activityVC, animated = true, completion = null)
}
