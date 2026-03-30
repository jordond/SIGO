package now.shouldigooutside.core.model.location

import androidx.compose.runtime.Immutable

/**
 * A location with a name and coordinates.
 *
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property name The name of the location, could be a formatted address or the latitude and longitude.
 * @property administrativeArea The state or province of the location.
 * @property country The country of the location.
 */
@Immutable
public data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String = "$latitude,$longitude",
    val administrativeArea: String? = null,
    val country: String? = null,
) {
    val isDefaultName: Boolean = name == "$latitude,$longitude"

    val roundedLatitude: String = latitude.toFixed(4)
    val roundedLongitude: String = longitude.toFixed(4)

    /**
     * A formatted string combining the administrative area and country, if available.
     *
     * Examples: "New York, United States", "Ontario, Canada", "New York", "United States", or null.
     */
    val subtitle: String?
        get() = listOfNotNull(administrativeArea, country)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(", ")

    public companion object {
        public fun create(
            latitude: Double,
            longitude: Double,
            name: String? = null,
            administrativeArea: String? = null,
            country: String? = null,
        ): Location =
            Location(
                latitude = latitude,
                longitude = longitude,
                name = name ?: "$latitude,$longitude",
                administrativeArea = administrativeArea,
                country = country,
            )
    }
}

private fun Double.toFixed(digits: Int): String =
    this.toString().let { value ->
        if (value.contains('.')) {
            val parts = value.split('.')
            val decimals = parts[1].take(digits).padEnd(digits, '0')
            "${parts[0]}.$decimals"
        } else {
            "$value.${"0".repeat(digits)}"
        }
    }
