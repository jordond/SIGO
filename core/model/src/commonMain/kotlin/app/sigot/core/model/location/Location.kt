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
}
