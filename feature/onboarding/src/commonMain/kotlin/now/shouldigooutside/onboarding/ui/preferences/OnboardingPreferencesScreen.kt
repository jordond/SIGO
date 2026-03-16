package now.shouldigooutside.onboarding.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.onboarding_preferences
import now.shouldigooutside.core.resources.onboarding_preferences_subtext
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.preferences.PreferencesList
import now.shouldigooutside.onboarding.ui.OnboardingScreenPreview
import now.shouldigooutside.onboarding.ui.navigation.OnboardingDestination
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OnboardingPreferencesScreen(model: OnboardingPreferencesModel = koinViewModel()) {
    val state by model.collectAsState()

    OnboardingPreferencesScreen(
        preferences = state.preferences,
        updatePreferences = model::update,
        temperatureRange = state.tempRange,
        maxWindSpeed = state.maxWindSpeed,
    )
}

@Composable
internal fun OnboardingPreferencesScreen(
    preferences: Preferences,
    updatePreferences: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text = Res.string.onboarding_preferences,
                    style = AppTheme.typography.header,
                    autoSize = AppTheme.typography.header.autoSize(),
                    maxLines = 1,
                )

                Text(
                    text = Res.string.onboarding_preferences_subtext,
                    modifier = Modifier.padding(start = 8.dp),
                    style = AppTheme.typography.body1,
                )
            }

            PreferencesList(
                preferences = preferences,
                updatePreferences = updatePreferences,
                temperatureRange = temperatureRange,
                maxWindSpeed = maxWindSpeed,
            )
        }
    }
}

@Preview
@Composable
private fun PreferencesScreenPreview() {
    OnboardingScreenPreview(OnboardingDestination.Preferences)
}
