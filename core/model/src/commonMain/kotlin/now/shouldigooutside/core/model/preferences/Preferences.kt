package now.shouldigooutside.core.model.preferences

import androidx.compose.runtime.Immutable

@Immutable
public data class Preferences(
    public val minTemperature: Int,
    public val maxTemperature: Int,
    public val includeApparentTemperature: Boolean,
    public val windSpeed: Int,
    public val rain: Boolean,
    public val snow: Boolean,
) {
    public companion object {
        public val default: Preferences = Preferences(
            minTemperature = 5,
            maxTemperature = 35,
            includeApparentTemperature = false,
            windSpeed = 30,
            rain = false,
            snow = false,
        )

        public fun defaultFor(activity: Activity): Preferences =
            when (activity) {
                is Activity.General -> default
                is Activity.Walking -> default.copy(
                    minTemperature = -10,
                    maxTemperature = 30,
                    windSpeed = 35,
                    rain = true,
                    snow = true,
                )
                is Activity.Running -> default.copy(
                    minTemperature = 10,
                    maxTemperature = 30,
                    windSpeed = 25,
                    rain = false,
                    snow = false,
                )
                is Activity.Cycling -> default.copy(
                    minTemperature = 10,
                    maxTemperature = 30,
                    windSpeed = 25,
                    rain = false,
                    snow = false,
                )
                is Activity.Hiking -> default.copy(
                    minTemperature = 5,
                    maxTemperature = 30,
                    windSpeed = 35,
                    rain = true,
                    snow = true,
                )
                is Activity.Swimming -> default.copy(
                    minTemperature = 20,
                    maxTemperature = 35,
                    windSpeed = 30,
                    rain = false,
                    snow = false,
                )
                is Activity.Custom -> default
            }
    }
}
