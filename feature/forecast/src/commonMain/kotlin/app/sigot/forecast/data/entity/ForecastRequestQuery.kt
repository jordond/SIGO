package app.sigot.forecast.data.entity

import app.sigot.core.model.location.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ForecastRequestQuery(
    @SerialName("lat")
    public val lat: Double,
    @SerialName("lon")
    public val lon: Double,
    @SerialName("name")
    public val name: String? = null,
)

public fun ForecastRequestQuery.toLocation(): Location =
    Location.create(
        latitude = lat,
        longitude = lon,
        name = name,
    )
