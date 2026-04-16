package now.shouldigooutside.settings.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.convertTemperature
import now.shouldigooutside.core.model.units.convertWindSpeed

@Serializable
internal data class PreferencesEntity(
    @Deprecated("Units are now stored in Settings, not per-preference. Kept for migration only.")
    @SerialName("units")
    val units: UnitsEntity? = null,
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
    @SerialName("max_aqi")
    val maxAqi: Int = 4,
    @SerialName("temperature_enabled")
    val temperatureEnabled: Boolean = true,
    @SerialName("wind_enabled")
    val windEnabled: Boolean = true,
    @SerialName("precipitation_enabled")
    val precipitationEnabled: Boolean = true,
    @SerialName("aqi_enabled")
    val aqiEnabled: Boolean = true,
)

@Suppress("DEPRECATION")
internal fun Preferences.toEntity() =
    PreferencesEntity(
        units = null,
        minTemperature = minTemperature,
        maxTemperature = maxTemperature,
        includeApparentTemperature = includeApparentTemperature,
        windSpeed = windSpeed,
        rain = rain,
        snow = snow,
        maxAqi = maxAqi.value,
        temperatureEnabled = temperatureEnabled,
        windEnabled = windEnabled,
        precipitationEnabled = precipitationEnabled,
        aqiEnabled = aqiEnabled,
    )

/**
 * Converts stored preferences to the model. If [units] is present (pre-migration data),
 * the values are converted from the stored unit system to Metric. If [units] is null,
 * the values are already in Metric and used as-is.
 */
@Suppress("DEPRECATION")
internal fun PreferencesEntity.toModel(): Preferences {
    val storedUnits = units?.toModel()

    // If no units stored, values are already in Metric — no conversion needed.
    if (storedUnits == null) {
        return Preferences(
            minTemperature = minTemperature,
            maxTemperature = maxTemperature,
            includeApparentTemperature = includeApparentTemperature,
            windSpeed = windSpeed,
            rain = rain,
            snow = snow,
            maxAqi = AirQuality(maxAqi),
            temperatureEnabled = temperatureEnabled,
            windEnabled = windEnabled,
            precipitationEnabled = precipitationEnabled,
            aqiEnabled = aqiEnabled,
        )
    }

    val metricUnits = Units.Metric

    return Preferences(
        minTemperature = convertTemperature(
            value = minTemperature.toDouble(),
            from = storedUnits.temperature,
            target = metricUnits.temperature,
        ).toInt(),
        maxTemperature = convertTemperature(
            value = maxTemperature.toDouble(),
            from = storedUnits.temperature,
            target = metricUnits.temperature,
        ).toInt(),
        includeApparentTemperature = includeApparentTemperature,
        windSpeed = convertWindSpeed(
            value = windSpeed.toDouble(),
            from = storedUnits.windSpeed,
            target = metricUnits.windSpeed,
        ).toInt(),
        rain = rain,
        snow = snow,
        maxAqi = AirQuality(maxAqi),
        temperatureEnabled = temperatureEnabled,
        windEnabled = windEnabled,
        precipitationEnabled = precipitationEnabled,
        aqiEnabled = aqiEnabled,
    )
}
