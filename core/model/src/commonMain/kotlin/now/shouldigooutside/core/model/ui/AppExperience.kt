package now.shouldigooutside.core.model.ui

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.settings.Settings

@Immutable
public data class AppExperience(
    val enableHaptics: Boolean,
    val use24HourFormat: Boolean,
    val includeAirQuality: Boolean,
    val enableActivities: Boolean,
) {
    public companion object {
        public val default: AppExperience = AppExperience(
            enableHaptics = true,
            use24HourFormat = false,
            includeAirQuality = true,
            enableActivities = true,
        )

        public fun from(settings: Settings): AppExperience =
            AppExperience(
                enableHaptics = settings.enableHaptics,
                use24HourFormat = settings.use24HourFormat,
                includeAirQuality = settings.includeAirQuality,
                enableActivities = settings.enableActivities,
            )
    }
}
