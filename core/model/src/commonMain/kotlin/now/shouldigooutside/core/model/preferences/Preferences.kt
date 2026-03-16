package now.shouldigooutside.core.model.preferences

import now.shouldigooutside.core.model.units.Units

public data class Preferences(
    public val units: Units,
    public val minTemperature: Int,
    public val maxTemperature: Int,
    public val includeApparentTemperature: Boolean,
    public val windSpeed: Int,
    public val rain: Boolean,
    public val snow: Boolean,
) {
    public companion object {
        public val default: Preferences = Preferences(
            units = Units.Metric,
            minTemperature = 5,
            maxTemperature = 35,
            includeApparentTemperature = false,
            windSpeed = 30,
            rain = false,
            snow = false,
        )
    }
}
