package app.sigot.core.config.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

public data class AppConfig(
    val locationCacheAge: Duration = Defaults.LOCATION_CACHE_AGE,
    val maxCacheAge: Duration = Defaults.MAX_CACHE_AGE,
    val minimumExecutionDelay: Duration = Defaults.MINIMUM_EXECUTION_DELAY,
) {
    internal constructor(
        locationCacheAge: Int?,
        maxCacheAge: Int?,
        minimumExecutionDelay: Int?,
    ) : this(
        locationCacheAge = locationCacheAge?.minutes ?: Defaults.LOCATION_CACHE_AGE,
        maxCacheAge = maxCacheAge?.minutes ?: Defaults.MAX_CACHE_AGE,
        minimumExecutionDelay = minimumExecutionDelay?.seconds ?: Defaults.MINIMUM_EXECUTION_DELAY,
    )

    internal companion object {
        internal object Defaults {
            val LOCATION_CACHE_AGE = 3.minutes
            val MAX_CACHE_AGE = 15.minutes
            val MINIMUM_EXECUTION_DELAY = 3.seconds
        }
    }
}
