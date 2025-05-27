package app.sigot.core.ui.navigation

import androidx.compose.runtime.Composable

@Composable
public actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    androidx.activity.compose.BackHandler(enabled = enabled, onBack = onBack)
}
