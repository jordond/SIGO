package app.sigot.core.model.location

/**
 * A location with a name and coordinates.
 *
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property name The name of the location, could be a formatted address or the latitude and longitude.
 */
public data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String = "$latitude,$longitude",
) {
    val isDefaultName: Boolean = name == "$latitude,$longitude"

    val roundedLatitude: String = latitude.toFixed(4)
    val roundedLongitude: String = longitude.toFixed(4)

    public companion object {
        public fun create(
            latitude: Double,
            longitude: Double,
            name: String? = null,
        ): Location =
            Location(
                latitude = latitude,
                longitude = longitude,
                name = name ?: "$latitude,$longitude",
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
