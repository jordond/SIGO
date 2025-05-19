package app.sigot.core.model.units

public enum class UnitPreset {
    SI,
    Metric,
    Imperial,
}

public val UnitPreset.units: Units
    get() = when (this) {
        UnitPreset.SI -> Units(
            temperature = TemperatureUnit.Kelvin,
            precipitation = PrecipitationUnit.Millimeter,
            windSpeed = WindSpeedUnit.MeterPerSecond,
            pressure = PressureUnit.HectoPascal,
        )
        UnitPreset.Metric -> Units(
            temperature = TemperatureUnit.Celsius,
            precipitation = PrecipitationUnit.Millimeter,
            windSpeed = WindSpeedUnit.KilometerPerHour,
            pressure = PressureUnit.HectoPascal,
        )
        UnitPreset.Imperial -> Units(
            temperature = TemperatureUnit.Fahrenheit,
            precipitation = PrecipitationUnit.Inch,
            windSpeed = WindSpeedUnit.MilePerHour,
            pressure = PressureUnit.InchMercury,
        )
    }
