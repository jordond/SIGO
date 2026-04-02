package now.shouldigooutside.core.domain.forecast

import kotlinx.coroutines.flow.Flow
import now.shouldigooutside.core.model.score.ActivityForecastScore

public interface GetActivitiesScoreUseCase {
    public fun scores(): List<ActivityForecastScore>

    public fun scoresFlow(): Flow<List<ActivityForecastScore>>
}
