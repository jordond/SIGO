package now.shouldigooutside.auto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.model.score.ActivityForecastScore

internal class FakeGetActivitiesScoreUseCase : GetActivitiesScoreUseCase {
    override fun scores(): List<ActivityForecastScore> = emptyList()

    override fun scoresFlow(): Flow<List<ActivityForecastScore>> = flowOf(emptyList())
}
