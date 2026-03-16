package now.shouldigooutside.core.config.firebase

import co.touchlab.kermit.Logger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfigValue
import dev.gitlive.firebase.remoteconfig.remoteConfig
import dev.stateholder.stateContainer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import now.shouldigooutside.core.config.AppConfigProvider
import now.shouldigooutside.core.config.model.AppConfig
import now.shouldigooutside.core.config.model.PrecipitationConfig
import now.shouldigooutside.core.config.model.UrlConfig
import now.shouldigooutside.core.platform.isDebug
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class FirebaseAppConfigProvider(
    private val scope: CoroutineScope,
) : AppConfigProvider {
    private var observeJob: Job? = null

    private val container = stateContainer(AppConfig())
    override val appConfig: Flow<AppConfig> = container.state

    override suspend fun initialize() {
        val config = Firebase.remoteConfig.apply {
            settings {
                minimumFetchInterval = if (isDebug) 0L.seconds else 10.minutes
            }

            try {
                fetchAndActivate()
            } catch (cause: Throwable) {
                Logger.e(cause) { "Failed to fetch and activate remote config: ${cause.message}" }
            }
        }

        container.update { config.all.toAppConfig() }

        observeJob = scope.launch {
            while (isActive) {
                try {
                    config.fetch()
                    val newConfig = config.all.toAppConfig()
                    if (container.state.value != newConfig) {
                        container.update { newConfig }
                    }
                } catch (cause: Throwable) {
                    if (cause is CancellationException) throw cause
                    Logger.w(cause) { "Failed to fetch remote config" }
                }

                delay(FETCH_DELAY)
            }
        }
    }

    private fun Map<String, FirebaseRemoteConfigValue>.toAppConfig(): AppConfig =
        AppConfig(
            locationCacheAge = get(KEY_LOCATION_CACHE_AGE_IN_MINUTES)?.asInt(),
            maxCacheAge = get(KEY_MAX_CACHE_AGE_IN_MINUTES)?.asInt(),
            minimumExecutionDelay = get(KEY_MINIMUM_EXECUTION_DELAY_IN_SECONDS)?.asInt(),
            scoreNearPercent = get(KEY_SCORE_NEAR_PERCENT)?.asDouble()?.toFloat(),
            scoreMaxNearReasons = get(KEY_SCORE_MAX_NEAR_REASONS)?.asInt(),
            maxForecastDays = get(KEY_MAX_FORECAST_DAYS)?.asInt(),
            precipitation = PrecipitationConfig(
                maxChance = get(KEY_PRECIPITATION_MAX_CHANCE)?.asDouble()?.toFloat(),
                lowAmountMm = get(KEY_PRECIPITATION_LOW_AMOUNT_MM)?.asInt(),
                moderateAmountMm = get(KEY_PRECIPITATION_MODERATE_AMOUNT_MM)?.asInt(),
                highAmountMm = get(KEY_PRECIPITATION_HIGH_AMOUNT_MM)?.asInt(),
            ),
            urlConfig = UrlConfig(
                root = get(KEY_URL_ROOT)?.asString() ?: UrlConfig.Defaults.ROOT,
                privacy = get(KEY_URL_PRIVACY)?.asString() ?: UrlConfig.Defaults.PRIVACY,
                terms = get(KEY_URL_TERMS)?.asString() ?: UrlConfig.Defaults.TERMS,
            ),
        )

    companion object {
        // Fetch delay is 10 minutes in production, 1 minute in debug
        private val FETCH_DELAY = if (isDebug) 60 * 1000L else 1000L * 60 * 10

        private const val KEY_LOCATION_CACHE_AGE_IN_MINUTES = "location_cache_age_in_minutes"
        private const val KEY_MAX_CACHE_AGE_IN_MINUTES = "max_cache_age_in_minutes"
        private const val KEY_MINIMUM_EXECUTION_DELAY_IN_SECONDS = "minimum_execution_delay_in_seconds"
        private const val KEY_SCORE_NEAR_PERCENT = "score_near_percent"
        private const val KEY_SCORE_MAX_NEAR_REASONS = "score_max_near_reasons"
        private const val KEY_MAX_FORECAST_DAYS = "max_forecast_days"

        // Precipitation
        private const val KEY_PRECIPITATION_MAX_CHANCE = "precipitation_max_chance"
        private const val KEY_PRECIPITATION_LOW_AMOUNT_MM = "precipitation_low_amount_mm"
        private const val KEY_PRECIPITATION_MODERATE_AMOUNT_MM = "precipitation_moderate_amount_mm"
        private const val KEY_PRECIPITATION_HIGH_AMOUNT_MM = "precipitation_high_amount_mm"

        // URL Config
        private const val KEY_URL_ROOT = "url_root"
        private const val KEY_URL_PRIVACY = "url_privacy"
        private const val KEY_URL_TERMS = "url_terms"
    }
}

private fun FirebaseRemoteConfigValue.asInt(): Int = asLong().toInt()
