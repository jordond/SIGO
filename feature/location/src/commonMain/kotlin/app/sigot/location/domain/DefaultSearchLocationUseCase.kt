package app.sigot.location.domain

import app.sigot.core.domain.location.SearchLocationUseCase
import app.sigot.core.model.location.Location
import app.sigot.core.platform.LocationManager
import co.touchlab.kermit.Logger

internal class DefaultSearchLocationUseCase(
    private val manager: LocationManager,
) : SearchLocationUseCase {
    private val logger = Logger.withTag("SearchLocationUseCase")

    override suspend fun search(query: String): List<Location> {
        if (query.isBlank()) return emptyList()

        val result = manager.autocomplete.search(query)
        return result
            .onFailed { error -> logger.e { "Autocomplete search failed: ${error.message}" } }
            .getOrNull()
            ?.map { place ->
                val name = place.locality
                    ?: place.subAdministrativeArea
                    ?: place.firstValue.takeUnless { it.isNullOrBlank() }
                Location.create(
                    latitude = place.coordinates.latitude,
                    longitude = place.coordinates.longitude,
                    name = name,
                )
            } ?: emptyList()
    }
}
