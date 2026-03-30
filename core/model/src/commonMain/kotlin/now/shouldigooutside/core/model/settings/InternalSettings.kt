package now.shouldigooutside.core.model.settings

import androidx.compose.runtime.Immutable
import now.shouldigooutside.build.BuildConfig

@Immutable
public data class InternalSettings(
    val enabled: Boolean = BuildConfig.ENABLE_INTERNAL_SETTINGS,
    val simulateFailure: Boolean = false,
    val backendApiUrl: String = DefaultBackendApiUrl,
    val apiKey: String? = BuildConfig.FORECAST_API_KEY.takeIf { it.isNotBlank() },
    val useDirectApi: Boolean = BuildConfig.USE_DIRECT_API,
) {
    public companion object {
        public const val DefaultBackendApiUrl: String = BuildConfig.BACKEND_URL
        public val DefaultWeatherApiToken: String? = BuildConfig.FORECAST_API_KEY.takeIf { it.isNotBlank() }
    }
}
