package now.shouldigooutside.core.domain.forecast

import kotlinx.coroutines.flow.Flow
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ForecastScore

public interface GetActivitiesScoreUseCase {
    public fun scores(): List<ActivityForecastScore>

    public fun scoresFlow(): Flow<List<ActivityForecastScore>>
}

public data class ActivityForecastScore(
    val activity: Activity,
    val preferences: Preferences,
    val score: ForecastScore,
)
