package now.shouldigooutside.core.ui.navigation

import androidx.compose.runtime.Composable

@Composable
public actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    androidx.compose.ui.backhandler
        .BackHandler(enabled = true, onBack = onBack)
}
