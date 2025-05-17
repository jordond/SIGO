package app.sigot.onboarding.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.ui.components.Text
import dev.stateholder.extensions.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PreferencesScreen(model: PreferencesModel = koinViewModel()) {
    val state by model.collectAsState()

    PreferencesScreen(
        preferences = state.preferences,
        updatePreferences = model::update,
    )
}

@Composable
internal fun PreferencesScreen(
    preferences: Preferences,
    updatePreferences: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Text("Preferences Screen")
    }
}
