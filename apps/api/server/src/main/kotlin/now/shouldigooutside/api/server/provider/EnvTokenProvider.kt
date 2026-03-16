package now.shouldigooutside.api.server.provider

import now.shouldigooutside.build.BuildConfig
import now.shouldigooutside.core.domain.forecast.ApiTokenProvider

internal class EnvTokenProvider : ApiTokenProvider {
    override fun provide(): String =
        System.getenv("FORECAST_API_KEY")
            ?: BuildConfig.FORECAST_API_KEY.takeIf { it.isNotBlank() }
            ?: error("FORECAST_API_KEY not set in environment or app-env.properties")
}
