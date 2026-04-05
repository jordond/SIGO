package now.shouldigooutside.settings.data.entity

import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Instant

@Serializable
internal data class SettingsEntity(
    @SerialName("first_launch")
    val firstLaunch: Long,
    @SerialName("theme")
    val theme: String,
    @SerialName("has_completed_onboarding")
    val hasCompletedOnboarding: Boolean,
    @SerialName("last_location")
    val lastLocation: LocationEntity? = null,
    @SerialName("last_location_update")
    val lastLocationUpdate: Long? = null,
    @SerialName("custom_location")
    val customLocation: LocationEntity? = null,
    @SerialName("use_custom_location")
    val useCustomLocation: Boolean = false,
    @SerialName("use_24_hour_format")
    val use24HourFormat: Boolean = false,
    @SerialName("include_air_quality")
    val includeAirQuality: Boolean = true,
    @SerialName("enable_activities")
    val enableActivities: Boolean = true,
    @SerialName("remember_activity")
    val rememberActivity: Boolean = true,
    @SerialName("units")
    val units: UnitsEntity? = null,
    @SerialName("selected_activity")
    val selectedActivity: String = ActivityEntity.General,
    @SerialName("widget_activity")
    val widgetActivity: String = ActivityEntity.General,
    @SerialName("activities")
    val activities: Map<String, PreferencesEntity> = emptyMap(),
    @Deprecated("Use activities with preferences instead")
    @SerialName("preferences")
    val preferences: PreferencesEntity? = null,
    @SerialName("enable_haptics")
    val enableHaptics: Boolean = true,
    @SerialName("internal_settings")
    val internalSettings: InternalSettingsEntity,
)

/**
 * We need to migrate old users who had units stored in the root of settings to the new structure where
 * units are stored per activity.
 *
 * This function checks if the old units field is present and uses it if available, otherwise it falls back
 * to the new structure.
 */
@Suppress("DEPRECATION")
internal fun SettingsEntity.toModel(): Settings {
    val oldUnits = (preferences?.units ?: activities[ActivityEntity.General]?.units)?.toModel()
    val units = oldUnits ?: Units.Metric

    // If the old preferences field is present and activities are empty,
    // we need to migrate it to the new structure.
    val activities = if (activities.isEmpty() && preferences != null) {
        mapOf(ActivityEntity.General to preferences)
    } else {
        activities
    }.toModel().toPersistentMap()

    val selected = mapActivityEntityToModel(selectedActivity)
    val selectedActivity = if (activities.containsKey(selected)) selected else Activity.General
    val widget = mapActivityEntityToModel(widgetActivity)
    val widgetActivity = if (activities.containsKey(widget)) widget else Activity.General

    return Settings(
        firstLaunch = Instant.fromEpochMilliseconds(firstLaunch),
        themeMode = ThemeMode.from(theme),
        hasCompletedOnboarding = hasCompletedOnboarding,
        units = units,
        selectedActivity = selectedActivity,
        widgetActivity = widgetActivity,
        activities = activities,
        use24HourFormat = use24HourFormat,
        lastLocation = lastLocation?.toModel(),
        lastLocationUpdate = lastLocationUpdate?.let { Instant.fromEpochMilliseconds(it) },
        customLocation = customLocation?.toModel(),
        useCustomLocation = useCustomLocation,
        enableHaptics = enableHaptics,
        includeAirQuality = includeAirQuality,
        enableActivities = enableActivities,
        internalSettings = internalSettings.toModel(),
        loaded = true,
    )
}

internal fun Settings.toEntity() =
    SettingsEntity(
        firstLaunch = firstLaunch.toEpochMilliseconds(),
        theme = themeMode.name,
        hasCompletedOnboarding = hasCompletedOnboarding,
        units = units.toEntity(),
        selectedActivity = selectedActivity.toEntity(),
        widgetActivity = widgetActivity.toEntity(),
        activities = activities.toEntity(),
        preferences = null, // Clear out old preferences field since we migrated to the new structure
        lastLocation = lastLocation?.toEntity(),
        lastLocationUpdate = lastLocationUpdate?.toEpochMilliseconds(),
        customLocation = customLocation?.toEntity(),
        useCustomLocation = useCustomLocation,
        use24HourFormat = use24HourFormat,
        enableHaptics = enableHaptics,
        includeAirQuality = includeAirQuality,
        enableActivities = enableActivities,
        internalSettings = internalSettings.toEntity(),
    )
