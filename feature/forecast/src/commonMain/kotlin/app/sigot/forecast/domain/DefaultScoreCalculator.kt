package app.sigot.forecast.domain

import app.sigot.core.config.AppConfigRepo
import app.sigot.core.config.model.PrecipitationConfig
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.domain.forecast.convert
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.forecast.PrecipitationType
import app.sigot.core.model.forecast.SevereWeatherRisk
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.score.ForecastScore
import app.sigot.core.model.score.ReasonValue
import app.sigot.core.model.score.Reasons
import app.sigot.core.model.score.Score
import app.sigot.core.model.score.ScoreResult
import app.sigot.core.model.units.Units
import co.touchlab.kermit.Logger

internal class DefaultScoreCalculator(
    private val appConfigRepo: AppConfigRepo,
) : ScoreCalculator {
    private val logger = Logger.withTag("ScoreCalculator")

    private val precipitationConfig: PrecipitationConfig
        get() = appConfigRepo.value.precipitation

    private val nearPercent: Float
        get() = appConfigRepo.value.scoreNearPercent

    private val maxNearReasons: Int
        get() = appConfigRepo.value.scoreMaxNearReasons

    override fun calculate(
        forecast: Forecast,
        preferences: Preferences,
    ): ForecastScore {
        val metricForecast = forecast.convert(Units.Metric)
        val result = ForecastScore(
            current = metricForecast.current.score(preferences),
            today = metricForecast.today.block.score(preferences),
            hours = metricForecast.today.hours.map { it.score(preferences) },
            days = metricForecast.days.map { it.block.score(preferences) },
        )
        logger.d {
            "Score calculation complete: current=${result.current.result}, today=${result.today.result}"
        }
        return result
    }

    private fun ForecastBlock.score(preferences: Preferences): Score {
        logger.d {
            "Scoring forecast block with temp=${temperature.value}°C, wind=${wind.speed}m/s, " +
                "precip=${precipitation.probability}%"
        }
        val reasons = Reasons(
            wind = windReason(preferences.windSpeed),
            temperature = temperatureReason(preferences),
            precipitation = precipitation(preferences),
            severeWeather = when (severeWeatherRisk) {
                SevereWeatherRisk.None -> ReasonValue.Inside
                SevereWeatherRisk.Low -> ReasonValue.Near
                SevereWeatherRisk.Moderate,
                SevereWeatherRisk.High,
                -> ReasonValue.Outside
            },
        )

        logger.d { "Severe weather risk: $severeWeatherRisk -> ${reasons.severeWeather}" }

        val result = reasons.toScoreResult()
        logger.d { "Final score result: $result with reasons=$reasons" }
        return Score(result = result, reasons = reasons)
    }

    private fun ForecastBlock.windReason(maxWindSpeed: Int): ReasonValue {
        val windSpeed = this.wind.speed
        logger.d { "Wind evaluation: current=$windSpeed, max=$maxWindSpeed" }

        if (windSpeed > maxWindSpeed) {
            return ReasonValue.Outside
        }

        // Near the limit (within [nearPercent] of maxWindSpeed)
        val nearThreshold = maxWindSpeed * (1 - nearPercent)
        if (windSpeed >= nearThreshold) {
            return ReasonValue.Near
        }

        // Wind is at comfortable levels
        return ReasonValue.Inside
    }

    private fun ForecastBlock.temperatureReason(preferences: Preferences): ReasonValue {
        val temperature =
            if (preferences.includeApparentTemperature) {
                temperature.feelsLike
            } else {
                temperature.value
            }

        val tempType = if (preferences.includeApparentTemperature) "feels like" else "actual"
        logger.d {
            "Temperature evaluation: $tempType=$temperature, " +
                "min=${preferences.minTemperature}, max=${preferences.maxTemperature}"
        }

        val minTemp = preferences.minTemperature
        val maxTemp = preferences.maxTemperature

        if (temperature < minTemp || temperature > maxTemp) {
            return ReasonValue.Outside
        }

        val range = maxTemp - minTemp
        val nearMinThreshold = minTemp + (range * nearPercent)
        val nearMaxThreshold = maxTemp - (range * nearPercent)
        if (temperature <= nearMinThreshold || temperature >= nearMaxThreshold) {
            return ReasonValue.Near
        }

        return ReasonValue.Inside
    }

    private fun ForecastBlock.precipitation(prefs: Preferences): ReasonValue {
        val isRain = precipitation.types.contains(PrecipitationType.Rain)
        val isSnow = precipitation.types.contains(PrecipitationType.Snow)
        val chance = precipitation.probability / 100f
        val nearChance = precipitationConfig.maxChance * (1 - nearPercent)
        val amount = precipitation.amount

        logger.d { "Precipitation: types=${precipitation.types}, chance=$chance, amount=$amount" }
        logger.d {
            "Precipitation: low=${precipitationConfig.lowAmountMm}, " +
                "moderate=${precipitationConfig.moderateAmountMm}"
        }
        logger.d { "Precipitation: maxChance=${precipitationConfig.maxChance}, nearChance=$nearChance" }
        logger.d { "User preferences: rain=${prefs.rain}, snow=${prefs.snow}" }

        return when {
            // User preferences for rain/snow
            chance > 0 && ((isRain && !prefs.rain) || (isSnow && !prefs.snow)) -> ReasonValue.Outside

            // Chance thresholds
            chance > precipitationConfig.maxChance -> ReasonValue.Outside
            chance > nearChance -> ReasonValue.Near

            // Amount thresholds
            amount > precipitationConfig.moderateAmountMm -> ReasonValue.Outside
            amount > precipitationConfig.lowAmountMm -> ReasonValue.Near

            else -> ReasonValue.Inside
        }
    }

    /**
     * Take all of the reason values in [Reasons] and determine whether the user should go outside
     *
     * The ReasonValue can be Inside, near, or outside their acceptable preferences. We should determine the
     * Yes, No, or Maybe result based on how many of the reasons are near and how many are outside. Maybe some
     * of the results should be weighted heavier than others, like SevereWeatherRisk
     */
    private fun Reasons.toScoreResult(): ScoreResult {
        // Severe weather is a primary safety concern, so check it first
        if (severeWeather == ReasonValue.Outside) {
            logger.d { "Severe weather is outside acceptable range, returning `No`" }
            return ScoreResult.No
        }

        val all = listOf(wind, temperature, precipitation, severeWeather)
        val (outside, near) = all.fold(0 to 0) { (outsideCount, nearCount), value ->
            when (value) {
                ReasonValue.Outside -> (outsideCount + 1) to nearCount
                ReasonValue.Near -> outsideCount to (nearCount + 1)
                else -> outsideCount to nearCount
            }
        }

        logger.d {
            listOf(
                "wind" to wind,
                "temperature" to temperature,
                "precipitation" to precipitation,
                "severeWeather" to severeWeather,
            ).joinToString { (name, value) -> "$name -> $value" }
        }

        logger.d { "Outside count: $outside, Near count: $near, Max near threshold: $maxNearReasons" }

        return when {
            // Any condition being outside limits means No
            outside > 0 -> ScoreResult.No
            // If severe weather is Near or any x+ conditions are Near, be cautious
            severeWeather == ReasonValue.Near || near > maxNearReasons -> ScoreResult.Maybe
            // Everything is good
            else -> ScoreResult.Yes
        }
    }
}
