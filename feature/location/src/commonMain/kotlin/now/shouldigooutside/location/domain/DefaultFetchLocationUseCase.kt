package now.shouldigooutside.location.domain

import now.shouldigooutside.core.domain.location.FetchLocationUseCase
import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.location.LocationResult

internal class DefaultFetchLocationUseCase(
    private val settingsRepo: SettingsRepo,
    private val locationRepo: LocationRepo,
) : FetchLocationUseCase {
    override suspend fun fetch(force: Boolean): LocationResult {
        TODO("Not yet implemented")
    }
}
