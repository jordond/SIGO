package app.sigot.core.model.units

public data class Units(
    val temperature: TemperatureUnit,
    val precipitation: PrecipitationUnit,
    val windSpeed: WindSpeedUnit,
    val pressure: PressureUnit,
) {
    public companion object {
        public val SI: Units = Units(
            temperature = TemperatureUnit.Kelvin,
            precipitation = PrecipitationUnit.Millimeter,
            windSpeed = WindSpeedUnit.MeterPerSecond,
            pressure = PressureUnit.HectoPascal,
        )

        public val Metric: Units = Units(
            temperature = TemperatureUnit.Celsius,
            precipitation = PrecipitationUnit.Millimeter,
            windSpeed = WindSpeedUnit.KilometerPerHour,
            pressure = PressureUnit.HectoPascal,
        )

        public val Imperial: Units = Units(
            temperature = TemperatureUnit.Fahrenheit,
            precipitation = PrecipitationUnit.Inch,
            windSpeed = WindSpeedUnit.MilePerHour,
            pressure = PressureUnit.InchMercury,
        )
    }
}
