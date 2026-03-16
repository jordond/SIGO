package now.shouldigooutside.core.platform

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.Priority
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState

internal actual fun createAutocomplete(): Autocomplete<Place> =
    object : Autocomplete<Place> {
        override val options get() = error("Not supported on this platform")

        override suspend fun search(query: String): AutocompleteResult<Place> =
            AutocompleteResult.NotSupported
    }

internal actual val geocoderSupported: Boolean = false

internal actual fun createGeocoder(): Geocoder =
    object : Geocoder {
        override val platformGeocoder: PlatformGeocoder
            get() = error("Not supported on this platform")

        override fun isAvailable(): Boolean = false

        override suspend fun forward(address: String): GeocoderResult<Coordinates> =
            GeocoderResult.NotSupported

        override suspend fun reverse(
            latitude: Double,
            longitude: Double,
        ): GeocoderResult<Place> = GeocoderResult.NotSupported
    }

internal actual fun locationPermissionController(): LocationPermissionController =
    object : LocationPermissionController {
        override fun hasPermission(): Boolean = false

        override suspend fun requirePermissionFor(priority: Priority): PermissionState =
            PermissionState.NotDetermined
    }
