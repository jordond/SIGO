package app.sigot.core.ui.navigation

import androidx.compose.runtime.Composable

@Composable
public expect fun BackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)

@Composable
public fun DisableBackButton(enabled: Boolean = true) {
    BackHandler(enabled = enabled, onBack = {})
}
