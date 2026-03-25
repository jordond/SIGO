package now.shouldigooutside

import androidx.compose.runtime.Composable
import now.shouldigooutside.ui.AppHost

@Composable
public fun App(onThemeChanged: @Composable (isDark: Boolean) -> Unit = {}) {
    AppHost(onThemeChanged = onThemeChanged)
}
