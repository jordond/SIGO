package now.shouldigooutside.data

import now.shouldigooutside.core.api.client.ApiUrlProvider
import now.shouldigooutside.core.domain.settings.SettingsRepo

internal class AppApiUrlProvider(
    private val settingsRepo: SettingsRepo,
) : ApiUrlProvider {
    override fun provide(): String = settingsRepo.settings.value.internalSettings.backendApiUrl
}
