package now.shouldigooutside.core.model.forecast

import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

internal val testBaseInstant: Instant = Instant.fromEpochSeconds(1_000_000)

internal fun hour(index: Int): Instant = testBaseInstant + index.hours

internal val TestLocation: Location = Location(
    latitude = 42.763,
    longitude = -81.878,
    name = "Test",
)

internal val EmptyReasons: Reasons = Reasons(
    wind = ReasonValue.Inside,
    temperature = ReasonValue.Inside,
    precipitation = ReasonValue.Inside,
    severeWeather = ReasonValue.Inside,
    airQuality = ReasonValue.Inside,
)

internal val yes: Score = Score(ScoreResult.Yes, EmptyReasons)
internal val no: Score = Score(ScoreResult.No, EmptyReasons)
internal val maybe: Score = Score(ScoreResult.Maybe, EmptyReasons)

internal fun block(instant: Instant): ForecastBlock =
    ForecastBlock(
        instant = instant,
        humidity = 0.0,
        cloudCoverPercent = 0,
        temperature = Temperature(value = 20.0, feelsLike = 20.0, max = 20.0, min = 20.0),
        precipitation = Precipitation(amount = 0.0, probability = 0, types = emptySet()),
        wind = Wind(
            speed = 0.0,
            gust = 0.0,
            directionDegree = 0.0,
            maxSpeed = 0.0,
            meanSpeed = 0.0,
            minSpeed = 0.0,
        ),
        pressure = 1013.0,
        uvIndex = 0,
        visibility = 10.0,
        severeWeatherRisk = SevereWeatherRisk.None,
        airQuality = AirQuality(1),
    )

internal fun forecast(hourCount: Int): Forecast {
    val hours = List(hourCount) { i -> block(testBaseInstant + i.hours) }
    val dayBlock = block(testBaseInstant)
    return Forecast(
        location = TestLocation,
        current = dayBlock,
        today = ForecastDay(block = dayBlock, hours = hours),
        days = emptyList(),
        alerts = persistentListOf(),
        instant = testBaseInstant,
    )
}

internal fun forecastScore(hours: List<Score>): ForecastScore =
    ForecastScore(
        current = no,
        hours = hours,
        today = no,
        days = emptyList(),
    )
