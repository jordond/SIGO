package now.shouldigooutside.forecast.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.location.Location

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
