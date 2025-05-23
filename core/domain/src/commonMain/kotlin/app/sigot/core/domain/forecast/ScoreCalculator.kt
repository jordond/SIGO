package app.sigot.core.domain.forecast

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.score.ForecastScore

public interface ScoreCalculator {
    public fun calculate(
        forecast: Forecast,
        preferences: Preferences,
    ): ForecastScore
}
