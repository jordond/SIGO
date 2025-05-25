package app.sigot.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.toebean.feature.webview.WebViewScreen
import kotlinx.serialization.Serializable

@Serializable
public data class WebViewRoute(
    public val title: String,
    public val url: String,
)

public fun NavGraphBuilder.webViewNavigation(onBack: () -> Unit) {
    composable<WebViewRoute> { entry ->
        val link = entry.toRoute<WebViewRoute>()
        WebViewScreen(
            onBack = onBack,
            title = link.title,
            url = link.url,
        )
    }
}
