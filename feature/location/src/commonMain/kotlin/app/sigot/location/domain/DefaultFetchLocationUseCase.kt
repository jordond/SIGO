package app.sigot.location.domain

import app.sigot.core.domain.location.FetchLocationUseCase
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.location.LocationResult

internal class DefaultFetchLocationUseCase(
    private val settingsRepo: SettingsRepo,
    private val locationRepo: LocationRepo,
) : FetchLocationUseCase {
    override suspend fun fetch(force: Boolean): LocationResult {
        TODO("Not yet implemented")
    }
}
