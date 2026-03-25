package now.shouldigooutside.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.ui.navigation.popUpScreen
import now.shouldigooutside.webview.WebViewScreen

@Serializable
public data class WebViewRoute(
    public val title: String? = null,
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
