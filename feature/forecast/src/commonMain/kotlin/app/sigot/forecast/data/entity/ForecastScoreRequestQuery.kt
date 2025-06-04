package app.sigot.forecast.data.entity

import app.sigot.core.model.location.Location
import app.sigot.core.model.preferences.Preferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ForecastScoreRequestQuery(
    @SerialName("lat")
    public val lat: Double,
    @SerialName("lon")
    public val lon: Double,
    @SerialName("name")
    public val name: String? = null,
    @SerialName("max_temp")
    public val maxTemp: Int? = null,
    @SerialName("min_temp")
    public val minTemp: Int? = null,
    @SerialName("max_wind")
    public val maxWind: Int? = null,
    @SerialName("allow")
    public val allow: List<String>? = null,
)

public fun ForecastScoreRequestQuery.toModels(): Pair<Location, Preferences> {
    val location = Location.create(lat, lon, name)
    return location to toPreferences()
}

private fun ForecastScoreRequestQuery.toPreferences(): Preferences {
    val default = Preferences.default
    return Preferences(
        units = default.units,
        minTemperature = minTemp ?: default.minTemperature,
        maxTemperature = maxTemp ?: default.maxTemperature,
        windSpeed = maxWind ?: default.windSpeed,
        rain = allow?.contains("rain") ?: default.rain,
        snow = allow?.contains("snow") ?: default.snow,
        includeApparentTemperature = default.includeApparentTemperature,
    )
}
