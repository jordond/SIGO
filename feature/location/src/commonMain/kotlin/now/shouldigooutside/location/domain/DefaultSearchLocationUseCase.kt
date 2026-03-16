package now.shouldigooutside.location.domain

import co.touchlab.kermit.Logger
import now.shouldigooutside.core.domain.location.SearchLocationUseCase
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.platform.LocationManager

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
            ?.mapNotNull { place ->
                val name = place.locality
                    ?: place.subAdministrativeArea
                    ?: place.administrativeArea
                    ?: place.firstValue.takeUnless { it.isNullOrBlank() }
                    ?: return@mapNotNull null
                Location.create(
                    latitude = place.coordinates.latitude,
                    longitude = place.coordinates.longitude,
                    name = name,
                    administrativeArea = place.administrativeArea,
                    country = place.country,
                )
            } ?: emptyList()
    }
}
