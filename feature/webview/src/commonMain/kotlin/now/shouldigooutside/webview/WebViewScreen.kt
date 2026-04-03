package now.shouldigooutside.webview

import androidx.compose.runtime.Composable
import com.multiplatform.webview.web.rememberWebViewState

@Composable
internal fun WebViewScreen(
    onBack: () -> Unit,
    title: String?,
    url: String,
) {
    val state = rememberWebViewState(url = url)

    WebViewContent(
        title = title,
        state = state,
        onClose = onBack,
    )
}
