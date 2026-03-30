package now.shouldigooutside.core.model.settings

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Clock
import kotlin.time.Instant

@Immutable
public class Settings(
    public val firstLaunch: Instant = Clock.System.now(),
    public val themeMode: ThemeMode = ThemeMode.Light,
    public val hasCompletedOnboarding: Boolean = false,
    public val lastLocation: Location? = null,
    public val lastLocationUpdate: Instant? = null,
    public val customLocation: Location? = null,
    public val useCustomLocation: Boolean = false,
    public val use24HourFormat: Boolean = false,
    public val includeAirQuality: Boolean = true,
    public val enableActivities: Boolean = true,
    public val units: Units = Units.Metric,
    public val selectedActivity: Activity = Activity.General,
    public val activities: PersistentMap<Activity, Preferences> =
        persistentMapOf(Activity.General to Preferences.default),
    public val enableHaptics: Boolean = true,
    public val internalSettings: InternalSettings = InternalSettings(),
    public val loaded: Boolean = false,
) {
    @Deprecated("Use preferences for specific activity instead")
    public val preferences: Preferences
        get() = activities[Activity.General] ?: error("General preferences wasn't set!")

    public fun updatePreferences(
        activity: Activity,
        value: Preferences,
    ): Settings {
        val current = activities[activity] ?: Preferences.default
        if (value == current) return this
        return fullCopy(
            activities = activities.toPersistentMap().put(activity, value),
        )
    }

    public fun add(
        activity: Activity,
        value: Preferences,
    ): Settings = fullCopy(activities = activities.toPersistentMap().put(activity, value))

    public fun remove(activity: Activity): Settings {
        if (activity is Activity.General) return this
        if (!activities.containsKey(activity)) return this

        val newSelected = if (selectedActivity == activity) {
            Activity.General
        } else {
            selectedActivity
        }

        return fullCopy(
            selectedActivity = newSelected,
            activities = activities.toPersistentMap().remove(activity),
        )
    }

    public fun copy(
        firstLaunch: Instant = this.firstLaunch,
        themeMode: ThemeMode = this.themeMode,
        hasCompletedOnboarding: Boolean = this.hasCompletedOnboarding,
        lastLocation: Location? = this.lastLocation,
        lastLocationUpdate: Instant? = this.lastLocationUpdate,
        customLocation: Location? = this.customLocation,
        useCustomLocation: Boolean = this.useCustomLocation,
        use24HourFormat: Boolean = this.use24HourFormat,
        includeAirQuality: Boolean = this.includeAirQuality,
        enableActivities: Boolean = this.enableActivities,
        units: Units = this.units,
        selectedActivity: Activity = this.selectedActivity,
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
            customLocation = customLocation,
            useCustomLocation = useCustomLocation,
            use24HourFormat = use24HourFormat,
            includeAirQuality = includeAirQuality,
            enableActivities = enableActivities,
            units = units,
            selectedActivity = selectedActivity,
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
        customLocation: Location? = this.customLocation,
        useCustomLocation: Boolean = this.useCustomLocation,
        use24HourFormat: Boolean = this.use24HourFormat,
        includeAirQuality: Boolean = this.includeAirQuality,
        enableActivities: Boolean = this.enableActivities,
        units: Units = this.units,
        selectedActivity: Activity = this.selectedActivity,
        activities: PersistentMap<Activity, Preferences> = this.activities,
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
            customLocation = customLocation,
            useCustomLocation = useCustomLocation,
            use24HourFormat = use24HourFormat,
            includeAirQuality = includeAirQuality,
            enableActivities = enableActivities,
            units = units,
            selectedActivity = selectedActivity,
            activities = activities,
            enableHaptics = enableHaptics,
            internalSettings = internalSettings,
            loaded = loaded,
        )

    override fun toString(): String =
        "Settings(firstLaunch=$firstLaunch, themeMode=$themeMode, use24HourFormat=$use24HourFormat, " +
            "hasCompletedOnboarding=$hasCompletedOnboarding, lastLocation=$lastLocation, " +
            "lastLocationUpdate=$lastLocationUpdate, customLocation=$customLocation, " +
            "useCustomLocation=$useCustomLocation, units=$units, selected=$selectedActivity, " +
            "activities=$activities, enableHaptics=$enableHaptics, internalSettings=$internalSettings, " +
            "includeAirQuality=$includeAirQuality, enableActivities=$enableActivities, loaded=$loaded)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Settings

        if (firstLaunch != other.firstLaunch) return false
        if (themeMode != other.themeMode) return false
        if (hasCompletedOnboarding != other.hasCompletedOnboarding) return false
        if (lastLocation != other.lastLocation) return false
        if (lastLocationUpdate != other.lastLocationUpdate) return false
        if (customLocation != other.customLocation) return false
        if (useCustomLocation != other.useCustomLocation) return false
        if (use24HourFormat != other.use24HourFormat) return false
        if (includeAirQuality != other.includeAirQuality) return false
        if (enableActivities != other.enableActivities) return false
        if (units != other.units) return false
        if (selectedActivity != other.selectedActivity) return false
        if (activities != other.activities) return false
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
        result = 31 * result + (customLocation?.hashCode() ?: 0)
        result = 31 * result + useCustomLocation.hashCode()
        result = 31 * result + use24HourFormat.hashCode()
        result = 31 * result + includeAirQuality.hashCode()
        result = 31 * result + enableActivities.hashCode()
        result = 31 * result + units.hashCode()
        result = 31 * result + selectedActivity.hashCode()
        result = 31 * result + activities.hashCode()
        result = 31 * result + enableHaptics.hashCode()
        result = 31 * result + internalSettings.hashCode()
        result = 31 * result + loaded.hashCode()
        return result
    }
}
