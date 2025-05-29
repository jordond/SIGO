package app.sigot.data.forecast

import app.sigot.core.domain.forecast.ApiTokenProvider
import app.sigot.core.domain.settings.SettingsRepo

internal class AppTokenProvider(
    private val settingsRepo: SettingsRepo,
) : ApiTokenProvider {
    override fun provide(): String =
        settingsRepo.settings.value.internalSettings.apiKey
            ?: error("No API key is set")
}
