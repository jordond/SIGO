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
    val minTemperature: Double,
    @SerialName("max_temp")
    val maxTemperature: Double,
    @SerialName("include_apparent_temp")
    val includeApparentTemperature: Boolean,
    @SerialName("wind_speed")
    val windSpeed: Double,
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
 * Converts stored preferences to the model. Storage contract: values are always in Metric.
 * If [units] is present (pre-migration data), values were stored in those units and are
 * converted to Metric here. Otherwise the raw Metric values are returned as-is.
 *
 * A separate one-shot settings-level migration handles the post-#21 era where values were
 * incorrectly written in user units with a null [units] field; see [SettingsEntity.toModel].
 */
@Suppress("DEPRECATION")
internal fun PreferencesEntity.toModel(): Preferences {
    val storedUnits = units?.toModel() ?: return Preferences(
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

    val metricUnits = Units.Metric

    return Preferences(
        minTemperature = convertTemperature(
            value = minTemperature,
            from = storedUnits.temperature,
            target = metricUnits.temperature,
        ),
        maxTemperature = convertTemperature(
            value = maxTemperature,
            from = storedUnits.temperature,
            target = metricUnits.temperature,
        ),
        includeApparentTemperature = includeApparentTemperature,
        windSpeed = convertWindSpeed(
            value = windSpeed,
            from = storedUnits.windSpeed,
            target = metricUnits.windSpeed,
        ),
        rain = rain,
        snow = snow,
        maxAqi = AirQuality(maxAqi),
        temperatureEnabled = temperatureEnabled,
        windEnabled = windEnabled,
        precipitationEnabled = precipitationEnabled,
        aqiEnabled = aqiEnabled,
    )
}

internal fun PreferencesEntity.migrateToMetric(from: Units): PreferencesEntity =
    copy(
        minTemperature = convertTemperature(minTemperature, from.temperature, Units.Metric.temperature),
        maxTemperature = convertTemperature(maxTemperature, from.temperature, Units.Metric.temperature),
        windSpeed = convertWindSpeed(windSpeed, from.windSpeed, Units.Metric.windSpeed),
    )
