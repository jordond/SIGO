package app.sigot.core.domain.forecast

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.score.ForecastScore
import kotlinx.coroutines.flow.Flow

// TODO: Is this needed anymore?
public interface GetScoreUseCase {
    public fun scoreFor(forecast: Forecast): ForecastScore

    public fun scoreForFlow(forecast: Forecast): Flow<ForecastScore>
}
