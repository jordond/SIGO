package now.shouldigooutside.test

import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ForecastScore

public class FakeScoreCalculator(
    public var result: ForecastScore = testForecastScore(),
) : ScoreCalculator {
    public var lastForecast: Forecast? = null
    public var lastPreferences: Preferences? = null
    public var lastIncludeAirQuality: Boolean? = null

    override fun calculate(
        forecast: Forecast,
        preferences: Preferences,
        includeAirQuality: Boolean,
    ): ForecastScore {
        lastForecast = forecast
        lastPreferences = preferences
        lastIncludeAirQuality = includeAirQuality
        return result
    }
}
