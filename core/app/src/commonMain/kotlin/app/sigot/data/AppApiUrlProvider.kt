package app.sigot.data

import app.sigot.core.api.client.ApiUrlProvider
import app.sigot.core.domain.settings.SettingsRepo

internal class AppApiUrlProvider(
    private val settingsRepo: SettingsRepo,
) : ApiUrlProvider {
    override fun provide(): String = settingsRepo.settings.value.internalSettings.backendApiUrl
}
