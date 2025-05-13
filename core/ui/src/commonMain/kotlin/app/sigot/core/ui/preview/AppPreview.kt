package app.sigot.core.ui.preview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Surface

@Composable
public fun AppPreview(
    modifier: Modifier = Modifier,
    useSurface: Boolean = true,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    AppTheme(
        isDarkTheme = isDarkTheme,
    ) {
        if (useSurface) {
            Surface {
                Box(modifier = modifier) {
                    content()
                }
            }
        } else {
            Box(modifier = modifier) {
                content()
            }
        }
    }
}
