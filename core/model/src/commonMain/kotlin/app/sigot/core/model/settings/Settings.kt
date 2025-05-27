package app.sigot.core.model.settings

import app.sigot.core.model.location.Location
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.ui.ThemeMode
import app.sigot.core.model.units.convertTemperature
import app.sigot.core.model.units.convertWindSpeed
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

public class Settings(
    public val firstLaunch: Instant = Clock.System.now(),
    public val themeMode: ThemeMode = ThemeMode.Light,
    public val hasCompletedOnboarding: Boolean = false,
    public val lastLocation: Location? = null,
    public val lastLocationUpdate: Instant? = null,
    public val preferences: Preferences = Preferences.default,
    public val enableHaptics: Boolean = true,
    public val internalSettings: InternalSettings = InternalSettings(),
    public val loaded: Boolean = false,
) {
    public fun updatePreferences(value: Preferences): Settings {
        if (value == preferences) return this

        if (value.units == preferences.units) return fullCopy(preferences = value)

        return fullCopy(
            preferences = value.copy(
                minTemperature = convertTemperature(
                    value = value.minTemperature.toDouble(),
                    from = preferences.units.temperature,
                    target = value.units.temperature,
                ).toInt(),
                maxTemperature = convertTemperature(
                    value = value.maxTemperature.toDouble(),
                    from = preferences.units.temperature,
                    target = value.units.temperature,
                ).toInt(),
                windSpeed = convertWindSpeed(
                    value = value.windSpeed.toDouble(),
                    from = preferences.units.windSpeed,
                    target = value.units.windSpeed,
                ).toInt(),
            ),
        )
    }

    public fun copy(
        firstLaunch: Instant = this.firstLaunch,
        themeMode: ThemeMode = this.themeMode,
        hasCompletedOnboarding: Boolean = this.hasCompletedOnboarding,
        lastLocation: Location? = this.lastLocation,
        lastLocationUpdate: Instant? = this.lastLocationUpdate,
        enableHaptics: Boolean = this.enableHaptics,
        internalSettings: InternalSettings = this.internalSettings,
        loaded: Boolean = this.loaded,
    ): Settings =
        fullCopy(
            firstLaunch = firstLaunch,
            themeMode = themeMode,
            hasCompletedOnboarding = hasCompletedOnboarding,
            lastLocation = lastLocation,
            lastLocationUpdate = lastLocationUpdate,
            enableHaptics = enableHaptics,
            internalSettings = internalSettings,
            loaded = loaded,
        )

    private fun fullCopy(
        firstLaunch: Instant = this.firstLaunch,
        themeMode: ThemeMode = this.themeMode,
        hasCompletedOnboarding: Boolean = this.hasCompletedOnboarding,
        lastLocation: Location? = this.lastLocation,
        lastLocationUpdate: Instant? = this.lastLocationUpdate,
        preferences: Preferences = this.preferences,
        enableHaptics: Boolean = this.enableHaptics,
        internalSettings: InternalSettings = this.internalSettings,
        loaded: Boolean = this.loaded,
    ): Settings =
        Settings(
            firstLaunch = firstLaunch,
            themeMode = themeMode,
            hasCompletedOnboarding = hasCompletedOnboarding,
            lastLocation = lastLocation,
            lastLocationUpdate = lastLocationUpdate,
            preferences = preferences,
            enableHaptics = enableHaptics,
            internalSettings = internalSettings,
            loaded = loaded,
        )

    override fun toString(): String =
        "Settings(firstLaunch=$firstLaunch, themeMode=$themeMode, " +
            "hasCompletedOnboarding=$hasCompletedOnboarding, lastLocation=$lastLocation, " +
            "lastLocationUpdate=$lastLocationUpdate, preferences=$preferences, " +
            "enableHaptics=$enableHaptics, internalSettings=$internalSettings, loaded=$loaded)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Settings

        if (firstLaunch != other.firstLaunch) return false
        if (themeMode != other.themeMode) return false
        if (hasCompletedOnboarding != other.hasCompletedOnboarding) return false
        if (lastLocation != other.lastLocation) return false
        if (lastLocationUpdate != other.lastLocationUpdate) return false
        if (preferences != other.preferences) return false
        if (enableHaptics != other.enableHaptics) return false
        if (internalSettings != other.internalSettings) return false
        if (loaded != other.loaded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstLaunch.hashCode()
        result = 31 * result + themeMode.hashCode()
        result = 31 * result + hasCompletedOnboarding.hashCode()
        result = 31 * result + (lastLocation?.hashCode() ?: 0)
        result = 31 * result + (lastLocationUpdate?.hashCode() ?: 0)
        result = 31 * result + preferences.hashCode()
        result = 31 * result + enableHaptics.hashCode()
        result = 31 * result + internalSettings.hashCode()
        result = 31 * result + loaded.hashCode()
        return result
    }
}
