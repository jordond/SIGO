package app.sigot.settings.data.entity

import app.sigot.core.model.preferences.Preferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PreferencesEntity(
    @SerialName("units")
    val units: UnitsEntity,
    @SerialName("use_24_hour_format")
    val use24HourFormat: Boolean,
    @SerialName("enable_location_updates")
    val enableLocationUpdates: Boolean,
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
        use24HourFormat = use24HourFormat,
        enableLocationUpdates = enableLocationUpdates,
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
        use24HourFormat = use24HourFormat,
        enableLocationUpdates = enableLocationUpdates,
        minTemperature = minTemperature,
        maxTemperature = maxTemperature,
        includeApparentTemperature = includeApparentTemperature,
        windSpeed = windSpeed,
        rain = rain,
        snow = snow,
    )
