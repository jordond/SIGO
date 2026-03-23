package now.shouldigooutside.core.ui.preview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import now.shouldigooutside.core.ui.AppTheme

@Composable
public fun AppPreview(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    AppTheme(
        isDarkTheme = isDarkTheme,
    ) {
        Box(modifier = modifier) {
            content()
        }
    }
}
