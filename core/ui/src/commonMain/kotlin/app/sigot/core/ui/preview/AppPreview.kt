package app.sigot.core.ui.preview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.sigot.core.ui.theme.AppTheme

@Composable
public fun AppPreview(
    modifier: Modifier = Modifier,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    AppTheme(
        useDarkTheme = useDarkTheme,
    ) {
        Box(modifier = modifier) {
            content()
        }
    }
}
