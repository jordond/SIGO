package now.shouldigooutside.core.domain.forecast

import co.touchlab.kermit.Logger
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.PrecipitationType
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.preferences.enabledMetrics
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.Metric
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.Units

public interface ScoreCalculator {
    public fun calculate(
        forecast: Forecast,
        preferences: Preferences,
        includeAirQuality: Boolean,
    ): ForecastScore
}

public class DefaultScoreCalculator(
    private val maxChance: Float = 0.4f,
    private val lowAmountMm: Int = 2,
    private val moderateAmountMm: Int = 5,
    private val nearPercent: Float = 0.05f,
    private val maxNearReasons: Int = 2,
) : ScoreCalculator {
    private val logger = Logger.withTag("ScoreCalculator")

    override fun calculate(
        forecast: Forecast,
        preferences: Preferences,
        includeAirQuality: Boolean,
    ): ForecastScore {
        val metricForecast = forecast.convert(Units.Metric)
        val result = ForecastScore(
            current = metricForecast.current.score(preferences, includeAirQuality),
            today = metricForecast.today.block.score(preferences, includeAirQuality),
            hours = metricForecast.today.hours.map { it.score(preferences, includeAirQuality) },
            days = metricForecast.days.map { it.block.score(preferences, includeAirQuality) },
        )
        logger.d {
            "Score calculation complete:\n" +
                "current=${result.current},\n" +
                "today=${result.today},\n" +
                "hours=${result.hours.joinToString(",")},\n" +
                "days=${result.days.joinToString(",")}"
        }
        return result
    }

    private fun ForecastBlock.score(
        preferences: Preferences,
        includeAirQuality: Boolean,
    ): Score {
        logger.d {
            "Scoring forecast block with temp=${temperature.value}°C, wind=${wind.speed}m/s, " +
                "precip=${precipitation.probability}%"
        }
        val reasons = Reasons(
            wind = windReason(preferences),
            temperature = temperatureReason(preferences),
            precipitation = precipitation(preferences),
            severeWeather = when (severeWeatherRisk) {
                SevereWeatherRisk.None -> ReasonValue.Inside
                SevereWeatherRisk.Low -> ReasonValue.Near
                SevereWeatherRisk.Moderate,
                SevereWeatherRisk.High,
                -> ReasonValue.Outside
            },
            airQuality = airQualityReason(preferences),
        )

        logger.d { "Severe weather risk: $severeWeatherRisk -> ${reasons.severeWeather}" }

        val enabled = preferences.enabledMetrics(includeAirQuality)
        val result = reasons.toScoreResult(enabled)
        logger.d { "Final score result: $result with reasons=$reasons" }
        return Score(result = result, reasons = reasons)
    }

    private fun ForecastBlock.windReason(preferences: Preferences): ReasonValue {
        val windSpeed = this.wind.speed
        val maxWindSpeed = preferences.windSpeed
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

        val minTemp = preferences.minTemperature
        val maxTemp = preferences.maxTemperature
        val tempType = if (preferences.includeApparentTemperature) "feels like" else "actual"
        logger.d {
            "Temperature evaluation: $tempType=$temperature, min=$minTemp, max=$maxTemp"
        }

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
        val nearChance = maxChance * (1 - nearPercent)
        val amount = precipitation.amount

        logger.d { "Precipitation: types=${precipitation.types}, chance=$chance, amount=$amount" }
        logger.d { "Precipitation: low=$lowAmountMm, moderate=$moderateAmountMm" }
        logger.d { "Precipitation: maxChance=$maxChance, nearChance=$nearChance" }
        logger.d { "User preferences: rain=${prefs.rain}, snow=${prefs.snow}" }

        return when {
            // User preferences for rain/snow
            chance > 0 && ((isRain && !prefs.rain) || (isSnow && !prefs.snow)) -> ReasonValue.Outside
            // Chance thresholds
            chance > maxChance -> ReasonValue.Outside
            chance > nearChance -> ReasonValue.Near
            // Amount thresholds
            amount > moderateAmountMm -> ReasonValue.Outside
            amount > lowAmountMm -> ReasonValue.Near
            else -> ReasonValue.Inside
        }
    }

    private fun ForecastBlock.airQualityReason(preferences: Preferences): ReasonValue {
        val aqi = this.airQuality
        val maxAqi = preferences.maxAqi
        logger.d { "AQI evaluation: current=${aqi.value}, max=${maxAqi.value}" }

        // No data available, don't penalize
        if (!aqi.hasData) return ReasonValue.Inside

        if (aqi.value > maxAqi.value) return ReasonValue.Outside

        val nearThreshold = maxAqi.value * (1 - nearPercent)
        if (aqi.value >= nearThreshold) return ReasonValue.Near

        return ReasonValue.Inside
    }

    /**
     * Take all the reason values in [Reasons] and determine whether the user should go outside
     *
     * The ReasonValue can be Inside, near, or outside their acceptable preferences. We should determine the
     * Yes, No, or Maybe result based on how many of the reasons are near and how many are outside. Maybe some
     * of the results should be weighted heavier than others, like SevereWeatherRisk
     */
    private fun Reasons.toScoreResult(enabled: Set<Metric>): ScoreResult {
        val severeEnabled = Metric.SevereWeather in enabled
        // Severe weather is a primary safety concern, so check it first
        if (severeEnabled && severeWeather == ReasonValue.Outside) {
            logger.d { "Severe weather is outside acceptable range, returning `No`" }
            return ScoreResult.No
        }

        val all = listOfNotNull(
            if (Metric.Wind in enabled) wind else null,
            if (Metric.Temperature in enabled) temperature else null,
            if (Metric.Precipitation in enabled) precipitation else null,
            if (severeEnabled) severeWeather else null,
            if (Metric.AirQuality in enabled) airQuality else null,
        )
        val (outside, near) = all.fold(0 to 0) { (outsideCount, nearCount), value ->
            when (value) {
                ReasonValue.Outside -> (outsideCount + 1) to nearCount
                ReasonValue.Near -> outsideCount to (nearCount + 1)
                else -> outsideCount to nearCount
            }
        }

        logger.d {
            listOfNotNull(
                if (Metric.Wind in enabled) "wind" to wind else null,
                if (Metric.Temperature in enabled) "temperature" to temperature else null,
                if (Metric.Precipitation in enabled) "precipitation" to precipitation else null,
                if (severeEnabled) "severeWeather" to severeWeather else null,
                if (Metric.AirQuality in enabled) "airQuality" to airQuality else null,
            ).joinToString { (name, value) -> "$name -> $value" }
        }

        logger.d { "Outside count: $outside, Near count: $near, Max near threshold: $maxNearReasons" }

        return when {
            // Any condition being outside limits means No
            outside > 0 -> ScoreResult.No
            // If severe weather is Near or any x+ conditions are Near, be cautious
            (severeEnabled && severeWeather == ReasonValue.Near) || near > maxNearReasons -> ScoreResult.Maybe
            // Everything is good
            else -> ScoreResult.Yes
        }
    }
}
