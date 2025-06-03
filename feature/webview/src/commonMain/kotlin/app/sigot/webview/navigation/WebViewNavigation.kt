package app.sigot.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

@Serializable
internal data class WebViewRoute(
    val title: String,
    val url: String,
)

public expect fun NavGraphBuilder.webViewNavigation(onBack: () -> Unit)

public expect fun NavHostController.navigateToWebview(title: String, url: String)
