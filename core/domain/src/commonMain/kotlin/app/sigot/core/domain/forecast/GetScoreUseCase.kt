package app.sigot.core.domain.forecast

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.score.ForecastScore
import kotlinx.coroutines.flow.Flow

public interface GetScoreUseCase {
    public fun scoreFor(forecast: Forecast): Flow<ForecastScore>
}
