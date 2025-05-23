package app.sigot.core.config.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

public data class AppConfig(
    val locationCacheAge: Duration = Defaults.LOCATION_CACHE_AGE,
    val maxCacheAge: Duration = Defaults.MAX_CACHE_AGE,
    val minimumExecutionDelay: Duration = Defaults.MINIMUM_EXECUTION_DELAY,
    val scoreNearPercent: Float = Defaults.SCORE_NEAR_PERCENT,
    val scoreMaxNearReasons: Int = Defaults.SCORE_MAX_NEAR_REASONS,
    val maxForecastDays: Int = Defaults.MAX_FORECAST_DAYS,
    val precipitation: PrecipitationConfig = PrecipitationConfig(),
) {
    internal constructor(
        locationCacheAge: Int?,
        maxCacheAge: Int?,
        minimumExecutionDelay: Int?,
        scoreNearPercent: Float?,
        scoreMaxNearReasons: Int?,
        maxForecastDays: Int?,
        precipitation: PrecipitationConfig?,
    ) : this(
        locationCacheAge = locationCacheAge?.minutes ?: Defaults.LOCATION_CACHE_AGE,
        maxCacheAge = maxCacheAge?.minutes ?: Defaults.MAX_CACHE_AGE,
        minimumExecutionDelay = minimumExecutionDelay?.seconds ?: Defaults.MINIMUM_EXECUTION_DELAY,
        scoreNearPercent = scoreNearPercent ?: Defaults.SCORE_NEAR_PERCENT,
        scoreMaxNearReasons = scoreMaxNearReasons ?: Defaults.SCORE_MAX_NEAR_REASONS,
        maxForecastDays = maxForecastDays ?: Defaults.MAX_FORECAST_DAYS,
        precipitation = precipitation ?: PrecipitationConfig(),
    )

    internal companion object {
        internal object Defaults {
            val LOCATION_CACHE_AGE = 3.minutes
            val MAX_CACHE_AGE = 15.minutes
            val MINIMUM_EXECUTION_DELAY = 3.seconds
            const val MAX_FORECAST_DAYS = 4
            const val SCORE_NEAR_PERCENT = 0.10f
            const val SCORE_MAX_NEAR_REASONS = 3
        }
    }
}
