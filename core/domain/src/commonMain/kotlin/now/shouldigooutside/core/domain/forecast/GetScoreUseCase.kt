package now.shouldigooutside.core.domain.forecast

import kotlinx.coroutines.flow.Flow
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ForecastScore

// TODO: Is this needed anymore?
public interface GetScoreUseCase {
    public fun scoreFor(forecast: Forecast): ForecastScore

    public fun scoreForFlow(forecast: Forecast): Flow<ForecastScore>
}
