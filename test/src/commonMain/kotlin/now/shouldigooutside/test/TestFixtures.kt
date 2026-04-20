package now.shouldigooutside.test

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastDay
import now.shouldigooutside.core.model.forecast.Precipitation
import now.shouldigooutside.core.model.forecast.PrecipitationType
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.forecast.Temperature
import now.shouldigooutside.core.model.forecast.Wind
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Instant

public fun testTemperature(
    value: Double = 20.0,
    feelsLike: Double = 20.0,
    max: Double = 25.0,
    min: Double = 15.0,
): Temperature =
    Temperature(
        value = value,
        feelsLike = feelsLike,
        max = max,
        min = min,
    )

public fun testWind(
    speed: Double = 10.0,
    gust: Double = 12.0,
    directionDegree: Double = 180.0,
    maxSpeed: Double = 12.0,
    meanSpeed: Double = 10.0,
    minSpeed: Double = 8.0,
): Wind =
    Wind(
        speed = speed,
        gust = gust,
        directionDegree = directionDegree,
        maxSpeed = maxSpeed,
        meanSpeed = meanSpeed,
        minSpeed = minSpeed,
    )

public fun testPrecipitation(
    amount: Double = 0.0,
    probability: Int = 0,
    types: Set<PrecipitationType> = emptySet(),
): Precipitation =
    Precipitation(
        amount = amount,
        probability = probability,
        types = types,
    )

public fun testForecastBlock(
    instant: Instant = Instant.fromEpochSeconds(0),
    humidity: Double = 50.0,
    cloudCoverPercent: Int = 20,
    temperature: Temperature = testTemperature(),
    precipitation: Precipitation = testPrecipitation(),
    wind: Wind = testWind(),
    pressure: Double = 1013.0,
    uvIndex: Int = 3,
    visibility: Double = 10.0,
    severeWeatherRisk: SevereWeatherRisk = SevereWeatherRisk.None,
    airQuality: AirQuality = AirQuality.None,
): ForecastBlock =
    ForecastBlock(
        instant = instant,
        humidity = humidity,
        cloudCoverPercent = cloudCoverPercent,
        temperature = temperature,
        precipitation = precipitation,
        wind = wind,
        pressure = pressure,
        uvIndex = uvIndex,
        visibility = visibility,
        severeWeatherRisk = severeWeatherRisk,
        airQuality = airQuality,
    )

public fun testForecastDay(
    block: ForecastBlock = testForecastBlock(),
    hours: List<ForecastBlock> = emptyList(),
): ForecastDay =
    ForecastDay(
        block = block,
        hours = hours,
    )

public fun testLocation(
    latitude: Double = 43.6532,
    longitude: Double = -79.3832,
    name: String = "Toronto",
    administrativeArea: String? = "Ontario",
    country: String? = "Canada",
): Location =
    Location(
        latitude = latitude,
        longitude = longitude,
        name = name,
        administrativeArea = administrativeArea,
        country = country,
    )

public fun testForecast(
    location: Location = testLocation(),
    current: ForecastBlock = testForecastBlock(),
    today: ForecastDay = testForecastDay(),
    days: List<ForecastDay> = emptyList(),
    alerts: PersistentList<Alert> = persistentListOf(),
    units: Units = Units.SI,
    instant: Instant = Instant.fromEpochSeconds(0),
): Forecast =
    Forecast(
        location = location,
        current = current,
        today = today,
        days = days,
        alerts = alerts,
        units = units,
        instant = instant,
    )

public fun testReasons(
    wind: ReasonValue = ReasonValue.Inside,
    temperature: ReasonValue = ReasonValue.Inside,
    precipitation: ReasonValue = ReasonValue.Inside,
    severeWeather: ReasonValue = ReasonValue.Inside,
    airQuality: ReasonValue = ReasonValue.Inside,
): Reasons =
    Reasons(
        wind = wind,
        temperature = temperature,
        precipitation = precipitation,
        severeWeather = severeWeather,
        airQuality = airQuality,
    )

public fun testScore(
    result: ScoreResult = ScoreResult.Yes,
    reasons: Reasons = testReasons(),
): Score =
    Score(
        result = result,
        reasons = reasons,
    )

public fun testForecastScore(
    current: Score = testScore(),
    hours: List<Score> = emptyList(),
    today: Score = testScore(),
    days: List<Score> = emptyList(),
): ForecastScore =
    ForecastScore(
        current = current,
        hours = hours,
        today = today,
        days = days,
    )
