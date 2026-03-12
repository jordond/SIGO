package app.sigot.settings.data.entity

import app.sigot.core.model.location.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LocationEntity(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("name")
    val name: String?,
    @SerialName("administrativeArea")
    val administrativeArea: String? = null,
    @SerialName("country")
    val country: String? = null,
)

internal fun Location.toEntity() =
    LocationEntity(
        latitude = latitude,
        longitude = longitude,
        name = if (isDefaultName) null else name,
        administrativeArea = administrativeArea,
        country = country,
    )

internal fun LocationEntity.toModel() =
    Location.create(
        latitude = latitude,
        longitude = longitude,
        name = name,
        administrativeArea = administrativeArea,
        country = country,
    )
