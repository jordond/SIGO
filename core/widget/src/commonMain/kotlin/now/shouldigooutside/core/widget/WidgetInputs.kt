package now.shouldigooutside.core.widget

import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.units.PrecipitationUnit
import now.shouldigooutside.core.model.units.PressureUnit
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit

/**
 * Serializable snapshot of widget-relevant settings, shared with the iOS widget extension
 * via the app group container so the extension can refresh independently of the host app.
 */
@Serializable
public data class WidgetInputs(
    val location: StoredLocation?,
    val units: StoredUnits,
    val widgetActivityKey: String,
    val customActivityName: String? = null,
    val preferences: StoredPreferences,
    val includeAirQuality: Boolean,
) {
    @Serializable
    public data class StoredLocation(
        val latitude: Double,
        val longitude: Double,
        val name: String,
        val administrativeArea: String? = null,
        val country: String? = null,
    )

    @Serializable
    public data class StoredUnits(
        val temperature: String,
        val precipitation: String,
        val windSpeed: String,
        val pressure: String,
    )

    @Serializable
    public data class StoredPreferences(
        val minTemperature: Int,
        val maxTemperature: Int,
        val includeApparentTemperature: Boolean,
        val windSpeed: Int,
        val rain: Boolean,
        val snow: Boolean,
        val maxAqi: Int,
        val temperatureEnabled: Boolean = true,
        val windEnabled: Boolean = true,
        val precipitationEnabled: Boolean = true,
        val aqiEnabled: Boolean = true,
    )

    public companion object {
        public fun from(settings: Settings): WidgetInputs {
            val widgetActivity = settings.widgetActivity
            val preferences = settings.activities[widgetActivity] ?: Preferences.default
            return WidgetInputs(
                location = settings.location?.toStored(),
                units = settings.units.toStored(),
                widgetActivityKey = widgetActivity.toKey(),
                customActivityName = (widgetActivity as? Activity.Custom)?.name,
                preferences = preferences.toStored(),
                includeAirQuality = settings.includeAirQuality,
            )
        }
    }
}

public fun WidgetInputs.StoredLocation.toModel(): Location =
    Location(
        latitude = latitude,
        longitude = longitude,
        name = name,
        administrativeArea = administrativeArea,
        country = country,
    )

public fun WidgetInputs.StoredUnits.toModel(): Units =
    Units(
        temperature = TemperatureUnit.valueOf(temperature),
        precipitation = PrecipitationUnit.valueOf(precipitation),
        windSpeed = WindSpeedUnit.valueOf(windSpeed),
        pressure = PressureUnit.valueOf(pressure),
    )

public fun WidgetInputs.StoredPreferences.toModel(): Preferences =
    Preferences(
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

public fun WidgetInputs.toActivity(): Activity =
    when (widgetActivityKey) {
        ActivityKeys.WALKING -> Activity.Walking
        ActivityKeys.RUNNING -> Activity.Running
        ActivityKeys.CYCLING -> Activity.Cycling
        ActivityKeys.HIKING -> Activity.Hiking
        ActivityKeys.SWIMMING -> Activity.Swimming
        ActivityKeys.CUSTOM -> Activity.Custom(customActivityName.orEmpty())
        else -> Activity.General
    }

private fun Location.toStored(): WidgetInputs.StoredLocation =
    WidgetInputs.StoredLocation(
        latitude = latitude,
        longitude = longitude,
        name = name,
        administrativeArea = administrativeArea,
        country = country,
    )

private fun Units.toStored(): WidgetInputs.StoredUnits =
    WidgetInputs.StoredUnits(
        temperature = temperature.name,
        precipitation = precipitation.name,
        windSpeed = windSpeed.name,
        pressure = pressure.name,
    )

private fun Preferences.toStored(): WidgetInputs.StoredPreferences =
    WidgetInputs.StoredPreferences(
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

private fun Activity.toKey(): String =
    when (this) {
        is Activity.General -> ActivityKeys.GENERAL
        is Activity.Walking -> ActivityKeys.WALKING
        is Activity.Running -> ActivityKeys.RUNNING
        is Activity.Cycling -> ActivityKeys.CYCLING
        is Activity.Hiking -> ActivityKeys.HIKING
        is Activity.Swimming -> ActivityKeys.SWIMMING
        is Activity.Custom -> ActivityKeys.CUSTOM
    }

private object ActivityKeys {
    const val GENERAL = "General"
    const val WALKING = "Walking"
    const val RUNNING = "Running"
    const val CYCLING = "Cycling"
    const val HIKING = "Hiking"
    const val SWIMMING = "Swimming"
    const val CUSTOM = "Custom"
}
