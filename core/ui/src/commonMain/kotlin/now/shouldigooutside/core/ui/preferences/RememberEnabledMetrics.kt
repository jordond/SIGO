package now.shouldigooutside.core.ui.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.preferences.enabledMetrics
import now.shouldigooutside.core.model.score.Metric
import now.shouldigooutside.core.ui.LocalAppExperience

@Composable
public fun rememberEnabledMetrics(preferences: Preferences): Set<Metric> {
    val includeAqi = LocalAppExperience.current.includeAirQuality
    return remember(preferences, includeAqi) {
        preferences.enabledMetrics(includeAqi)
    }
}
