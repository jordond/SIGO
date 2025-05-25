package app.toebean.feature.webview

import androidx.compose.runtime.Composable
import app.sigot.webview.WebViewContent
import com.multiplatform.webview.web.rememberWebViewState

@Composable
internal fun WebViewScreen(
    onBack: () -> Unit,
    title: String,
    url: String,
) {
    val state = rememberWebViewState(url = url)

    WebViewContent(
        title = title,
        state = state,
        onClose = onBack,
    )
}
