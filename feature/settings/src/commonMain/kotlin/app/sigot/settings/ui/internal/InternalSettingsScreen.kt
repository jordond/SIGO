package app.sigot.settings.ui.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.sigot.core.model.settings.Settings
import app.sigot.core.resources.Res
import app.sigot.core.resources.settings_internal_title
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.preview.AppPreview
import app.sigot.settings.ui.components.SettingsTopBar
import dev.stateholder.extensions.collectAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun InternalSettingsScreen(model: InternalSettingsModel = koinViewModel()) {
    val state by model.collectAsState()

    InternalSettingsScreen(
        settings = state.settings,
    )
}

@Composable
internal fun InternalSettingsScreen(
    settings: Settings,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(
                text = Res.string.settings_internal_title,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
        }
    }
}

@Preview
@Composable
private fun InternalSettingsScreenPreview() {
    AppPreview {
        InternalSettingsScreen(
            settings = Settings(),
        )
    }
}
