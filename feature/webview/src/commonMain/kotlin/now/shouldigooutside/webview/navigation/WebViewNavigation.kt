package now.shouldigooutside.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import app.toebean.feature.webview.WebViewScreen
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.ui.navigation.popUpScreen

@Serializable
public data class WebViewRoute(
    public val title: String,
    public val url: String,
)

public fun NavGraphBuilder.webViewNavigation(onBack: () -> Unit) {
    popUpScreen<WebViewRoute> { entry ->
        val link = entry.toRoute<WebViewRoute>()
        WebViewScreen(
            onBack = onBack,
            title = link.title,
            url = link.url,
        )
    }
}
