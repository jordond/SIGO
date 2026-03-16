package now.shouldigooutside.settings.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.preferences.Preferences

@Serializable
internal data class PreferencesEntity(
    @SerialName("units")
    val units: UnitsEntity,
    @SerialName("min_temp")
    val minTemperature: Int,
    @SerialName("max_temp")
    val maxTemperature: Int,
    @SerialName("include_apparent_temp")
    val includeApparentTemperature: Boolean,
    @SerialName("wind_speed")
    val windSpeed: Int,
    @SerialName("rain")
    val rain: Boolean,
    @SerialName("snow")
    val snow: Boolean,
)

internal fun Preferences.toEntity() =
    PreferencesEntity(
        units = units.toEntity(),
        minTemperature = minTemperature,
        maxTemperature = maxTemperature,
        includeApparentTemperature = includeApparentTemperature,
        windSpeed = windSpeed,
        rain = rain,
        snow = snow,
    )

internal fun PreferencesEntity.toModel() =
    Preferences(
        units = units.toModel(),
        minTemperature = minTemperature,
        maxTemperature = maxTemperature,
        includeApparentTemperature = includeApparentTemperature,
        windSpeed = windSpeed,
        rain = rain,
        snow = snow,
    )
