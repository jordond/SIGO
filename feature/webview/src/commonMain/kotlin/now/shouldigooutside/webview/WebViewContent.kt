package now.shouldigooutside.webview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.close
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Scaffold
import now.shouldigooutside.core.ui.components.progressindicators.LinearProgressIndicator
import now.shouldigooutside.core.ui.components.topbar.TopBar
import now.shouldigooutside.core.ui.ktx.get

@Composable
internal fun WebViewContent(
    title: String,
    state: WebViewState,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigator = rememberWebViewNavigator()

    Scaffold(
        containerColor = AppTheme.colors.surface,
        topBar = {
            TopBar(
                title = {
                    Title(text = title)
                },
                navigationIcon = {
                    NavIcon(
                        icon = Icons.Default.Close,
                        contentDescription = Res.string.close.get(),
                        onClick = onClose,
                    )
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
        ) {
            val loadingState = state.loadingState
            if (loadingState is LoadingState.Loading) {
                LinearProgressIndicator(
                    progress = { loadingState.progress },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            WebView(
                state = state,
                navigator = navigator,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
