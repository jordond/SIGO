package now.shouldigooutside.core.domain.forecast

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.PrecipitationType
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastBlock
import now.shouldigooutside.test.testForecastDay
import now.shouldigooutside.test.testPrecipitation
import now.shouldigooutside.test.testTemperature
import now.shouldigooutside.test.testWind
import kotlin.test.Test

class DefaultScoreCalculatorTest {
    private val defaultPreferences = Preferences(
        minTemperature = 10,
        maxTemperature = 30,
        includeApparentTemperature = false,
        windSpeed = 20,
        rain = true,
        snow = true,
        maxAqi = AirQuality(5),
    )

    private val calculator = DefaultScoreCalculator()

    private fun scoreBlock(
        block: ForecastBlock,
        preferences: Preferences = defaultPreferences,
        includeAirQuality: Boolean = false,
    ) = calculator
        .calculate(
            forecast = testForecast(
                current = block,
                today = testForecastDay(block = block),
                units = Units.Metric,
            ),
            preferences = preferences,
            includeAirQuality = includeAirQuality,
        ).current

    @Test
    fun windInsideReturnsInside() {
        val block = testForecastBlock(wind = testWind(speed = 5.0))
        val score = scoreBlock(block)
        score.reasons.wind shouldBe ReasonValue.Inside
    }

    @Test
    fun windAboveMaxReturnsOutside() {
        val block = testForecastBlock(wind = testWind(speed = 21.0))
        val score = scoreBlock(block)
        score.reasons.wind shouldBe ReasonValue.Outside
    }

    @Test
    fun windJustAboveMaxReturnsOutside() {
        val block = testForecastBlock(wind = testWind(speed = 20.01))
        val score = scoreBlock(block)
        score.reasons.wind shouldBe ReasonValue.Outside
    }

    @Test
    fun windNearThresholdReturnsNear() {
        val block = testForecastBlock(wind = testWind(speed = 19.5))
        val score = scoreBlock(block)
        score.reasons.wind shouldBe ReasonValue.Near
    }

    @Test
    fun windAtExactMaxReturnsNear() {
        val block = testForecastBlock(wind = testWind(speed = 20.0))
        val score = scoreBlock(block)
        score.reasons.wind shouldBe ReasonValue.Near
    }

    @Test
    fun windBelowNearReturnsInside() {
        val block = testForecastBlock(wind = testWind(speed = 18.0))
        val score = scoreBlock(block)
        score.reasons.wind shouldBe ReasonValue.Inside
    }

    @Test
    fun tempInsideReturnsInside() {
        val block = testForecastBlock(temperature = testTemperature(value = 20.0))
        val score = scoreBlock(block)
        score.reasons.temperature shouldBe ReasonValue.Inside
    }

    @Test
    fun tempBelowMinReturnsOutside() {
        val block = testForecastBlock(temperature = testTemperature(value = 5.0))
        val score = scoreBlock(block)
        score.reasons.temperature shouldBe ReasonValue.Outside
    }

    @Test
    fun tempAboveMaxReturnsOutside() {
        val block = testForecastBlock(temperature = testTemperature(value = 35.0))
        val score = scoreBlock(block)
        score.reasons.temperature shouldBe ReasonValue.Outside
    }

    @Test
    fun tempNearMinReturnsNear() {
        val block = testForecastBlock(temperature = testTemperature(value = 10.5))
        val score = scoreBlock(block)
        score.reasons.temperature shouldBe ReasonValue.Near
    }

    @Test
    fun tempNearMaxReturnsNear() {
        val block = testForecastBlock(temperature = testTemperature(value = 29.5))
        val score = scoreBlock(block)
        score.reasons.temperature shouldBe ReasonValue.Near
    }

    @Test
    fun tempAtExactMinReturnsNear() {
        val block = testForecastBlock(temperature = testTemperature(value = 10.0))
        val score = scoreBlock(block)
        score.reasons.temperature shouldBe ReasonValue.Near
    }

    @Test
    fun tempAtExactMaxReturnsNear() {
        val block = testForecastBlock(temperature = testTemperature(value = 30.0))
        val score = scoreBlock(block)
        score.reasons.temperature shouldBe ReasonValue.Near
    }

    @Test
    fun tempUsesFeelsLikeWhenPreferenceSet() {
        val block = testForecastBlock(
            temperature = testTemperature(value = 20.0, feelsLike = 5.0),
        )
        val prefs = defaultPreferences.copy(
            minTemperature = 10,
            maxTemperature = 30,
            includeApparentTemperature = true,
        )
        val score = scoreBlock(block, prefs)
        score.reasons.temperature shouldBe ReasonValue.Outside
    }

    @Test
    fun noPrecipReturnsInside() {
        val block = testForecastBlock(precipitation = testPrecipitation(amount = 0.0, probability = 0))
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Inside
    }

    @Test
    fun rainDisabledWithRainReturnsOutside() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(
                probability = 10,
                types = setOf(PrecipitationType.Rain),
            ),
        )
        val prefs = defaultPreferences.copy(rain = false)
        val score = scoreBlock(block, prefs)
        score.reasons.precipitation shouldBe ReasonValue.Outside
    }

    @Test
    fun snowDisabledWithSnowReturnsOutside() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(
                probability = 10,
                types = setOf(PrecipitationType.Snow),
            ),
        )
        val prefs = defaultPreferences.copy(snow = false)
        val score = scoreBlock(block, prefs)
        score.reasons.precipitation shouldBe ReasonValue.Outside
    }

    @Test
    fun chanceAboveMaxReturnsOutside() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(probability = 41, types = setOf(PrecipitationType.Rain)),
        )
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Outside
    }

    @Test
    fun chanceNearMaxReturnsNear() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(probability = 39, types = setOf(PrecipitationType.Rain)),
        )
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Near
    }

    @Test
    fun chanceAtExactMaxReturnsNear() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(probability = 40, types = setOf(PrecipitationType.Rain)),
        )
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Near
    }

    @Test
    fun chanceAtExactNearThresholdReturnsInside() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(probability = 38, types = setOf(PrecipitationType.Rain)),
        )
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Inside
    }

    @Test
    fun amountAboveModerateReturnsOutside() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(amount = 6.0, probability = 0),
        )
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Outside
    }

    @Test
    fun amountAboveLowReturnsNear() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(amount = 3.0, probability = 0),
        )
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Near
    }

    @Test
    fun amountAtExactModerateReturnsNear() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(amount = 5.0, probability = 0),
        )
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Near
    }

    @Test
    fun amountAtExactLowReturnsInside() {
        val block = testForecastBlock(
            precipitation = testPrecipitation(amount = 2.0, probability = 0),
        )
        val score = scoreBlock(block)
        score.reasons.precipitation shouldBe ReasonValue.Inside
    }

    @Test
    fun noDataReturnsInside() {
        val block = testForecastBlock(airQuality = AirQuality(0))
        val score = scoreBlock(block, includeAirQuality = true)
        score.reasons.airQuality shouldBe ReasonValue.Inside
    }

    @Test
    fun belowMaxReturnsInside() {
        val block = testForecastBlock(airQuality = AirQuality(2))
        val prefs = defaultPreferences.copy(maxAqi = AirQuality(5))
        val score = scoreBlock(block, prefs, includeAirQuality = true)
        score.reasons.airQuality shouldBe ReasonValue.Inside
    }

    @Test
    fun aboveMaxReturnsOutside() {
        val block = testForecastBlock(airQuality = AirQuality(7))
        val prefs = defaultPreferences.copy(maxAqi = AirQuality(5))
        val score = scoreBlock(block, prefs, includeAirQuality = true)
        score.reasons.airQuality shouldBe ReasonValue.Outside
    }

    @Test
    fun nearThresholdReturnsNear() {
        val block = testForecastBlock(airQuality = AirQuality(5))
        val prefs = defaultPreferences.copy(maxAqi = AirQuality(5))
        val score = scoreBlock(block, prefs, includeAirQuality = true)
        score.reasons.airQuality shouldBe ReasonValue.Near
    }

    @Test
    fun noneReturnsInside() {
        val block = testForecastBlock(severeWeatherRisk = SevereWeatherRisk.None)
        val score = scoreBlock(block)
        score.reasons.severeWeather shouldBe ReasonValue.Inside
    }

    @Test
    fun lowReturnsNear() {
        val block = testForecastBlock(severeWeatherRisk = SevereWeatherRisk.Low)
        val score = scoreBlock(block)
        score.reasons.severeWeather shouldBe ReasonValue.Near
    }

    @Test
    fun moderateReturnsOutside() {
        val block = testForecastBlock(severeWeatherRisk = SevereWeatherRisk.Moderate)
        val score = scoreBlock(block)
        score.reasons.severeWeather shouldBe ReasonValue.Outside
    }

    @Test
    fun highReturnsOutside() {
        val block = testForecastBlock(severeWeatherRisk = SevereWeatherRisk.High)
        val score = scoreBlock(block)
        score.reasons.severeWeather shouldBe ReasonValue.Outside
    }

    @Test
    fun allInsideIsYes() {
        val block = testForecastBlock(
            temperature = testTemperature(value = 20.0),
            wind = testWind(speed = 5.0),
            precipitation = testPrecipitation(amount = 0.0, probability = 0),
            severeWeatherRisk = SevereWeatherRisk.None,
            airQuality = AirQuality(0),
        )
        val score = scoreBlock(block, includeAirQuality = true)
        score.result shouldBe ScoreResult.Yes
    }

    @Test
    fun anyOutsideIsNo() {
        val block = testForecastBlock(wind = testWind(speed = 100.0))
        val score = scoreBlock(block)
        score.result shouldBe ScoreResult.No
    }

    @Test
    fun severeWeatherNearIsMaybe() {
        val block = testForecastBlock(
            temperature = testTemperature(value = 20.0),
            wind = testWind(speed = 5.0),
            precipitation = testPrecipitation(amount = 0.0, probability = 0),
            severeWeatherRisk = SevereWeatherRisk.Low,
            airQuality = AirQuality(0),
        )
        val score = scoreBlock(block, includeAirQuality = false)
        score.result shouldBe ScoreResult.Maybe
    }

    @Test
    fun moreThanMaxNearIsMaybe() {
        val block = testForecastBlock(
            temperature = testTemperature(value = 29.5),
            wind = testWind(speed = 19.5),
            precipitation = testPrecipitation(probability = 39, types = setOf(PrecipitationType.Rain)),
            severeWeatherRisk = SevereWeatherRisk.None,
            airQuality = AirQuality(0),
        )
        val score = scoreBlock(block, includeAirQuality = false)
        score.result shouldBe ScoreResult.Maybe
    }

    @Test
    fun returnsScoresForAllPeriods() {
        val hourBlock = testForecastBlock(wind = testWind(speed = 5.0))
        val todayBlock = testForecastBlock(wind = testWind(speed = 5.0))
        val dayBlock = testForecastBlock(wind = testWind(speed = 5.0))
        val currentBlock = testForecastBlock(wind = testWind(speed = 5.0))

        val forecast = testForecast(
            current = currentBlock,
            today = testForecastDay(
                block = todayBlock,
                hours = listOf(hourBlock, hourBlock),
            ),
            days = listOf(
                testForecastDay(block = dayBlock),
                testForecastDay(block = dayBlock),
            ),
            units = Units.Metric,
        )

        val result = calculator.calculate(forecast, defaultPreferences, includeAirQuality = false)

        result.hours.size shouldBe 2
        result.days.size shouldBe 2
        result.current.result shouldBe ScoreResult.Yes
        result.today.result shouldBe ScoreResult.Yes
    }

    @Test
    fun airQualityDisabledIgnoresAqi() {
        val block = testForecastBlock(
            temperature = testTemperature(value = 20.0),
            wind = testWind(speed = 5.0),
            precipitation = testPrecipitation(amount = 0.0, probability = 0),
            severeWeatherRisk = SevereWeatherRisk.None,
            airQuality = AirQuality(10),
        )
        val prefs = defaultPreferences.copy(maxAqi = AirQuality(3))
        val score = scoreBlock(block, prefs, includeAirQuality = false)
        score.result shouldBe ScoreResult.Yes
    }
}
