package app.sigot.core.model.units

public sealed interface BaseUnit

public data class Units(
    val temperature: TemperatureUnit,
    val precipitation: PrecipitationUnit,
    val windSpeed: WindSpeedUnit,
    val pressure: PressureUnit,
) {
    public companion object {
        public val SI: Units = UnitPreset.SI.units
        public val Metric: Units = UnitPreset.Metric.units
        public val Imperial: Units = UnitPreset.Imperial.units
    }
}
