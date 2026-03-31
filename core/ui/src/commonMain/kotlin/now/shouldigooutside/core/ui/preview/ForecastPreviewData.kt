package now.shouldigooutside.core.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import now.shouldigooutside.core.domain.forecast.DefaultScoreCalculator
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
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

public object ForecastPreviewData {
    public fun sunny(instant: Instant = Clock.System.now()): ForecastBlock =
        ForecastBlock(
            instant = instant,
            humidity = 20.0,
            cloudCoverPercent = 0,
            temperature = Temperature(
                value = 20.0,
                feelsLike = 25.0,
                max = 25.0,
                min = 20.0,
            ),
            precipitation = Precipitation(
                amount = 0.0, // mm
                probability = 0,
                types = emptySet(),
            ),
            wind = Wind(
                speed = 10.0, // km/h
                gust = 10.0,
                directionDegree = 45.0,
                maxSpeed = 15.0,
                meanSpeed = 12.0,
                minSpeed = 5.0,
            ),
            pressure = 1012.0, // hPa, typical sea level pressure
            uvIndex = 5,
            visibility = 20.0, // km
            airQuality = AirQuality(3),
            severeWeatherRisk = SevereWeatherRisk.None,
        )

    public fun rainy(instant: Instant = Clock.System.now()): ForecastBlock =
        sunny(instant).copy(
            cloudCoverPercent = 80,
            temperature = Temperature(
                value = 15.0,
                feelsLike = 14.0,
                max = 18.0,
                min = 12.0,
            ),
            precipitation = Precipitation(
                amount = 5.0, // 5 mm of rain
                probability = 90, // 90% chance of rain
                types = setOf(PrecipitationType.Rain),
            ),
            uvIndex = 1,
            visibility = 5.0, // km, reduced visibility due to rain
        )

    public fun snowy(instant: Instant = Clock.System.now()): ForecastBlock =
        sunny(instant).copy(
            cloudCoverPercent = 90,
            temperature = Temperature(
                value = (-2.0),
                feelsLike = (-5.0),
                max = 0.0,
                min = (-4.0),
            ),
            precipitation = Precipitation(
                amount = 10.0, // 10 mm of snow (water equivalent)
                probability = 85, // 85% chance of snow
                types = setOf(PrecipitationType.Snow),
            ),
            wind = Wind(
                // Slightly increased wind for a snowy feel
                speed = 20.0, // km/h
                gust = 25.0,
                directionDegree = 310.0,
                maxSpeed = 25.0,
                meanSpeed = 22.0,
                minSpeed = 15.0,
            ),
            uvIndex = 0,
            visibility = 2.0, // km, reduced visibility due to snow
        )

    public fun windy(instant: Instant = Clock.System.now()): ForecastBlock =
        sunny(instant).copy(
            temperature = Temperature(
                // Cooler due to wind
                value = 10.0,
                feelsLike = 7.0,
                max = 12.0,
                min = 8.0,
            ),
            wind = Wind(
                speed = 40.0, // km/h, significantly windy
                gust = 55.0,
                directionDegree = 270.0, // Westerly wind
                maxSpeed = 55.0,
                meanSpeed = 45.0,
                minSpeed = 30.0,
            ),
            uvIndex = 3, // Can still be UV even if windy
        )

    public fun hot(instant: Instant = Clock.System.now()): ForecastBlock =
        sunny(instant).copy(
            humidity = 60.0, // Higher humidity can make it feel hotter
            temperature = Temperature(
                value = 35.0,
                feelsLike = 38.0, // Feels hotter due to humidity or other factors
                max = 37.0,
                min = 28.0,
            ),
            uvIndex = 10, // High UV index
        )

    public fun cold(instant: Instant = Clock.System.now()): ForecastBlock =
        sunny(instant).copy(
            humidity = 30.0,
            cloudCoverPercent = 10, // Can be cold and clear
            temperature = Temperature(
                value = (-10.0),
                feelsLike = (-15.0), // Wind chill can make it feel colder
                max = (-8.0),
                min = (-12.0),
            ),
            wind = Wind(
                // Add some wind for wind chill effect
                speed = 15.0,
                gust = 20.0,
                directionDegree = 0.0, // Northerly wind
                maxSpeed = 20.0,
                meanSpeed = 18.0,
                minSpeed = 10.0,
            ),
            uvIndex = 1, // Low UV index in cold weather
            visibility = 15.0,
        )

    public fun severeWeather(
        level: SevereWeatherRisk = SevereWeatherRisk.High,
        instant: Instant = Clock.System.now(),
    ): ForecastBlock =
        rainy(instant).copy(
            temperature = Temperature(
                value = 25.0,
                feelsLike = 27.0,
                max = 28.0,
                min = 22.0,
            ),
            precipitation = Precipitation(
                amount = 15.0, // Heavier rain
                probability = 95,
                types = setOf(PrecipitationType.Rain, PrecipitationType.Hail), // Could include hail
            ),
            wind = Wind(
                // Stronger, gusty winds
                speed = 30.0,
                gust = 60.0, // High gusts
                directionDegree = 180.0, // Southerly, often brings unstable air
                maxSpeed = 60.0,
                meanSpeed = 40.0,
                minSpeed = 20.0,
            ),
            severeWeatherRisk = level,
            uvIndex = 4, // Can be high before storms
            visibility = 3.0, // Reduced visibility
        )

    public val floodRiskAlert: Alert = Alert(
        title = "Flood risk",
        description = "* WHAT...Minor coastal flooding expected.\n\n" +
            "* WHERE...Bayshore locations along the San Francisco Bay and San\n" +
            "Pablo Bay.\n\n" +
            "* WHEN...From 10 PM this evening to 2 AM PDT Wednesday.\n\n" +
            "* IMPACTS...Flooding of lots, parks, and roads with only\n" +
            "isolated road closures expected.\n\n" +
            "* ADDITIONAL DETAILS...Low lying areas within the San Francisco\n" +
            "Bay Area may see minor coastal flooding as a result during high\n" +
            "tide. San Francisco high tide is 6.88 ft at 12:02 AM Wednesday.\n",
    )

    public fun createSunnyForecast(
        instant: Instant = Clock.System.now(),
        units: Units = Units.Metric,
        location: Location = Location(-81.878, 42.7632, "London, ON"),
        alerts: List<Alert> = emptyList(),
    ): Forecast =
        createForecastFrom(
            block = sunny(instant),
            instant = instant,
            units = units,
            location = location,
            alerts = alerts,
        )

    public fun createRainyForecast(
        instant: Instant = Clock.System.now(),
        units: Units = Units.Metric,
        location: Location = Location(-81.878, 42.7632, "London, ON"),
        alerts: List<Alert> = emptyList(),
    ): Forecast =
        createForecastFrom(
            block = rainy(instant),
            instant = instant,
            units = units,
            location = location,
            alerts = alerts,
        )

    public fun createColdForecast(
        instant: Instant = Clock.System.now(),
        units: Units = Units.Metric,
        location: Location = Location(-81.878, 42.7632, "London, ON"),
        alerts: List<Alert> = emptyList(),
    ): Forecast =
        createForecastFrom(
            block = cold(instant),
            instant = instant,
            units = units,
            location = location,
            alerts = alerts,
        )

    public fun createWindyForecast(
        instant: Instant = Clock.System.now(),
        units: Units = Units.Metric,
        location: Location = Location(-81.878, 42.7632, "London, ON"),
        alerts: List<Alert> = emptyList(),
    ): Forecast =
        createForecastFrom(
            block = windy(instant),
            instant = instant,
            units = units,
            location = location,
            alerts = alerts,
        )

    public fun createForecastFrom(
        block: ForecastBlock,
        instant: Instant = Clock.System.now(),
        units: Units = Units.Metric,
        location: Location = Location(-81.878, 42.7632, "London, ON"),
        alerts: List<Alert> = emptyList(),
    ): Forecast {
        val hours = listOf(
            block.copy(instant = instant.plus(1.hours)),
            block.copy(instant = instant.plus(2.hours)),
            block.copy(instant = instant.plus(3.hours)),
            block.copy(instant = instant.plus(4.hours)),
            block.copy(instant = instant.plus(5.hours)),
        )
        return Forecast(
            location = location,
            current = block,
            today = ForecastDay(block, hours),
            days = listOf(ForecastDay(block, hours)),
            alerts = alerts,
            units = units,
            instant = instant,
        )
    }

    public fun createForecast(
        instant: Instant = Clock.System.now(),
        units: Units = Units.Metric,
        location: Location = Location(-81.878, 42.7632, "London, ON"),
        current: ForecastBlock = sunny(instant),
        today: ForecastBlock = sunny(instant),
        hours: List<ForecastBlock> = listOf(
            sunny(instant.plus(1.hours)),
            sunny(instant.plus(2.hours)),
            sunny(instant.plus(3.hours)),
            sunny(instant.plus(4.hours)),
            sunny(instant.plus(5.hours)),
        ),
        days: List<ForecastDay> = listOf(
            ForecastDay(sunny(instant.plus(1.days)), hours),
        ),
        alerts: List<Alert> = emptyList(),
    ): Forecast =
        Forecast(
            location = location,
            current = current,
            today = ForecastDay(today, hours),
            days = days,
            alerts = alerts,
            units = units,
            instant = instant,
        )

    private val calculator = DefaultScoreCalculator()

    public fun score(
        forecast: Forecast,
        preferences: Preferences = Preferences.default,
        includeAirQuality: Boolean = true,
    ): ForecastScore = calculator.calculate(forecast, preferences, includeAirQuality)

    public class ForecastBlockPreviewParameterProvider : PreviewParameterProvider<ForecastBlock> {
        override val values: Sequence<ForecastBlock> = sequenceOf(sunny(), rainy(), snowy(), hot(), cold())
    }
}
