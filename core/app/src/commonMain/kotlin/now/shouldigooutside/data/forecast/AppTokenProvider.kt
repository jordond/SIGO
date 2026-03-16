package now.shouldigooutside.data.forecast

import now.shouldigooutside.core.domain.forecast.ApiTokenProvider
import now.shouldigooutside.core.domain.settings.SettingsRepo

internal class AppTokenProvider(
    private val settingsRepo: SettingsRepo,
) : ApiTokenProvider {
    override fun provide(): String =
        settingsRepo.settings.value.internalSettings.apiKey
            ?: error("No API key is set")
}
