package app.sigot.core.model.preferences

import app.sigot.core.model.units.Units

public data class Preferences(
    val units: Units,
    val use24HourFormat: Boolean,
    val minTemperature: Int,
    val maxTemperature: Int,
    val includeApparentTemperature: Boolean,
    val windSpeed: Int,
    val rain: Boolean,
    val snow: Boolean,
) {
    public companion object {
        public val default: Preferences = Preferences(
            units = Units.Metric,
            use24HourFormat = false,
            minTemperature = 5,
            maxTemperature = 35,
            includeApparentTemperature = false,
            windSpeed = 30,
            rain = false,
            snow = false,
        )
    }
}
