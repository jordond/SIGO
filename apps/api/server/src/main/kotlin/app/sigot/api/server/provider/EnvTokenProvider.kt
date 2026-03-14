package app.sigot.api.server.provider

import app.sigot.core.domain.forecast.ApiTokenProvider

internal class EnvTokenProvider : ApiTokenProvider {
    override fun provide(): String =
        System.getenv("FORECAST_API_KEY")
            ?: error("FORECAST_API_KEY environment variable is not set")
}
