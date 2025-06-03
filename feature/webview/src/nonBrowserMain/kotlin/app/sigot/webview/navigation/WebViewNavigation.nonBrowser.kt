package app.sigot.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import app.sigot.core.ui.navigation.popUpScreen
import app.sigot.webview.WebViewScreen

public actual fun NavGraphBuilder.webViewNavigation(onBack: () -> Unit) {
    popUpScreen<WebViewRoute> { entry ->
        val link = entry.toRoute<WebViewRoute>()
        WebViewScreen(
            onBack = onBack,
            title = link.title,
            url = link.url,
        )
    }
}

public actual fun NavHostController.navigateToWebview(title: String, url: String) {
    navigate(WebViewRoute(title, url))
}
