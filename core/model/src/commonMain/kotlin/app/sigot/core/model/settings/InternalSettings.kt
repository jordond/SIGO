package app.sigot.core.model.settings

import app.sigot.build.BuildKonfig

public data class InternalSettings(
    val enabled: Boolean = BuildKonfig.ENABLE_INTERNAL_SETTINGS,
    val simulateFailure: Boolean = false,
    val backendApiUrl: String = DefaultBackendApiUrl,
    val apiKey: String? = BuildKonfig.FORECAST_API_KEY.takeIf { it.isNotBlank() },
    // This is configured via app-env.properties
    @Suppress("SimplifyBooleanWithConstants")
    val useDirectApi: Boolean = BuildKonfig.USE_DIRECT_API || apiKey != null,
) {
    public companion object {
        public const val DefaultBackendApiUrl: String = BuildKonfig.BACKEND_URL
        public val DefaultWeatherApiToken: String? = BuildKonfig.FORECAST_API_KEY.takeIf { it.isNotBlank() }
    }
}
