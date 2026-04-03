package now.shouldigooutside.core.model.settings

import androidx.compose.runtime.Immutable
import dev.drewhamilton.poko.Poko
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

@Poko
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
    public val rememberActivity: Boolean = true,
    public val units: Units = Units.Metric,
    public val selectedActivity: Activity = Activity.General,
    public val widgetActivity: Activity = Activity.General,
    public val activities: PersistentMap<Activity, Preferences> =
        persistentMapOf(Activity.General to Preferences.default),
    public val enableHaptics: Boolean = true,
    public val internalSettings: InternalSettings = InternalSettings(),
    public val loaded: Boolean = false,
) {
    public val location: Location?
        get() = if (useCustomLocation) customLocation else lastLocation

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

        val newWidget = if (widgetActivity == activity) {
            Activity.General
        } else {
            widgetActivity
        }

        return fullCopy(
            selectedActivity = newSelected,
            widgetActivity = newWidget,
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
        rememberActivity: Boolean = this.rememberActivity,
        units: Units = this.units,
        selectedActivity: Activity = this.selectedActivity,
        widgetActivity: Activity = this.widgetActivity,
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
            rememberActivity = rememberActivity,
            units = units,
            selectedActivity = selectedActivity,
            widgetActivity = widgetActivity,
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
        rememberActivity: Boolean = this.rememberActivity,
        units: Units = this.units,
        selectedActivity: Activity = this.selectedActivity,
        widgetActivity: Activity = this.widgetActivity,
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
            widgetActivity = widgetActivity,
            rememberActivity = rememberActivity,
            activities = activities,
            enableHaptics = enableHaptics,
            internalSettings = internalSettings,
            loaded = loaded,
        )
}
