package app.sigot.core.model.settings

import app.sigot.build.BuildKonfig

public data class InternalSettings(
    val enabled: Boolean = false,
    val simulateFailure: Boolean = false,
    val backendApiUrl: String = DefaultBackendApiUrl,
    val apiKey: String? = BuildKonfig.WEATHER_API_TOKEN.takeIf { it.isNotBlank() },
    val useDirectApi: Boolean = apiKey != null,
) {
    public companion object {
        public const val DefaultBackendApiUrl: String = BuildKonfig.BACKEND_URL
        public val DefaultWeatherApiToken: String? = BuildKonfig.WEATHER_API_TOKEN.takeIf { it.isNotBlank() }
    }
}
