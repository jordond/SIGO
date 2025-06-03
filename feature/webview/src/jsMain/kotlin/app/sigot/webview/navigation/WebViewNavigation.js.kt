package app.sigot.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import kotlinx.browser.window

public actual fun NavGraphBuilder.webViewNavigation(onBack: () -> Unit) {
    // No-op since we can't/don't need to show the webview since we are in a browser
}

public actual fun NavHostController.navigateToWebview(title: String, url: String) {
    window.open(url, "_blank", "noopener,noreferrer")
}
