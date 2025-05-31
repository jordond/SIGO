package app.sigot.settings.data.entity

import app.sigot.core.model.settings.Settings
import app.sigot.core.model.ui.ThemeMode
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("use_24_hour_format")
    val use24HourFormat: Boolean = false,
    @SerialName("preferences")
    val preferences: PreferencesEntity,
    @SerialName("enable_haptics")
    val enableHaptics: Boolean = true,
    @SerialName("internal_settings")
    val internalSettings: InternalSettingsEntity,
)

internal fun SettingsEntity.toModel() =
    Settings(
        firstLaunch = Instant.fromEpochMilliseconds(firstLaunch),
        themeMode = ThemeMode.from(theme),
        hasCompletedOnboarding = hasCompletedOnboarding,
        preferences = preferences.toModel(),
        use24HourFormat = use24HourFormat,
        lastLocation = lastLocation?.toModel(),
        lastLocationUpdate = lastLocationUpdate?.let { Instant.fromEpochMilliseconds(it) },
        enableHaptics = enableHaptics,
        internalSettings = internalSettings.toModel(),
        loaded = true,
    )

internal fun Settings.toEntity() =
    SettingsEntity(
        firstLaunch = firstLaunch.toEpochMilliseconds(),
        theme = themeMode.name,
        hasCompletedOnboarding = hasCompletedOnboarding,
        preferences = preferences.toEntity(),
        lastLocation = lastLocation?.toEntity(),
        lastLocationUpdate = lastLocationUpdate?.toEpochMilliseconds(),
        use24HourFormat = use24HourFormat,
        enableHaptics = enableHaptics,
        internalSettings = internalSettings.toEntity(),
    )
