package now.shouldigooutside.core.model.score

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences

@Immutable
public data class ActivityForecastScore(
    val activity: Activity,
    val preferences: Preferences,
    val score: ForecastScore,
)
