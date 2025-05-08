package app.sigot.core.model.preferences.units

public data class Units(
    public val wind: WindUnit = WindUnit.Kph,
    public val temperature: TemperatureUnit = TemperatureUnit.Celsius,
    public val precipitation: PrecipitationUnit = PrecipitationUnit.Mm,
    public val distance: DistanceUnit = DistanceUnit.Km,
) {
    public companion object {
        public val Metric: Units = Units(
            wind = WindUnit.Kph,
            temperature = TemperatureUnit.Celsius,
            precipitation = PrecipitationUnit.Mm,
            distance = DistanceUnit.Km,
        )

        public val Imperial: Units = Units(
            wind = WindUnit.Mph,
            temperature = TemperatureUnit.Fahrenheit,
            precipitation = PrecipitationUnit.Inch,
            distance = DistanceUnit.Mile,
        )
    }
}
