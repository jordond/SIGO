package now.shouldigooutside.core.ui.preview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import now.shouldigooutside.core.model.ui.AppExperience
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalAppExperience

@Composable
public fun AppPreview(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    experience: AppExperience = AppExperience.default,
    content: @Composable () -> Unit,
) {
    AppTheme(
        isDarkTheme = isDarkTheme,
    ) {
        Box(modifier = modifier) {
            CompositionLocalProvider(
                LocalAppExperience provides experience,
            ) {
                content()
            }
        }
    }
}
