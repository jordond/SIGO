package app.sigot.core.model.units

private const val KELVIN_TO_CELSIUS = 273.15
private const val MILLIMETER_TO_INCH = 25.4
private const val MPS_TO_KPH = 3.6
private const val MPS_TO_MPH = 2.23694
private const val KPH_TO_MPH = 0.621371
private const val MPH_TO_KPH = 1.60934
private const val HPA_TO_INCH_MERCURY = 0.02953

public val Double.kelvin: Double
    get() = this + KELVIN_TO_CELSIUS

public val Int.kelvin: Double
    get() = this.toDouble().kelvin

public fun convertTemperature(
    value: Double,
    from: TemperatureUnit,
    target: TemperatureUnit,
): Double =
    when (from) {
        TemperatureUnit.Kelvin -> when (target) {
            TemperatureUnit.Kelvin -> value
            TemperatureUnit.Celsius -> value - KELVIN_TO_CELSIUS
            TemperatureUnit.Fahrenheit -> (value - KELVIN_TO_CELSIUS) * 9 / 5 + 32
        }
        TemperatureUnit.Celsius -> when (target) {
            TemperatureUnit.Kelvin -> value + KELVIN_TO_CELSIUS
            TemperatureUnit.Celsius -> value
            TemperatureUnit.Fahrenheit -> value * 9 / 5 + 32
        }
        TemperatureUnit.Fahrenheit -> when (target) {
            TemperatureUnit.Kelvin -> (value - 32) * 5 / 9 + KELVIN_TO_CELSIUS
            TemperatureUnit.Celsius -> (value - 32) * 5 / 9
            TemperatureUnit.Fahrenheit -> value
        }
    }

public fun convertPrecipitation(
    value: Double,
    from: PrecipitationUnit,
    target: PrecipitationUnit,
): Double =
    when (from) {
        PrecipitationUnit.Millimeter -> when (target) {
            PrecipitationUnit.Millimeter -> value
            PrecipitationUnit.Inch -> value / MILLIMETER_TO_INCH
        }
        PrecipitationUnit.Inch -> when (target) {
            PrecipitationUnit.Millimeter -> value * MILLIMETER_TO_INCH
            PrecipitationUnit.Inch -> value
        }
    }

public fun convertWindSpeed(
    value: Double,
    from: WindSpeedUnit,
    target: WindSpeedUnit,
): Double =
    when (from) {
        WindSpeedUnit.MeterPerSecond -> when (target) {
            WindSpeedUnit.MeterPerSecond -> value
            WindSpeedUnit.KilometerPerHour -> value * MPS_TO_KPH
            WindSpeedUnit.MilePerHour -> value * MPS_TO_MPH
        }
        WindSpeedUnit.KilometerPerHour -> when (target) {
            WindSpeedUnit.MeterPerSecond -> value / MPS_TO_KPH
            WindSpeedUnit.KilometerPerHour -> value
            WindSpeedUnit.MilePerHour -> value * KPH_TO_MPH
        }
        WindSpeedUnit.MilePerHour -> when (target) {
            WindSpeedUnit.MeterPerSecond -> value / MPS_TO_MPH
            WindSpeedUnit.KilometerPerHour -> value * MPH_TO_KPH
            WindSpeedUnit.MilePerHour -> value
        }
    }

public fun convertPressure(
    value: Double,
    from: PressureUnit,
    target: PressureUnit,
): Double =
    when (from) {
        PressureUnit.HectoPascal -> when (target) {
            PressureUnit.HectoPascal -> value
            PressureUnit.InchMercury -> value * HPA_TO_INCH_MERCURY
        }
        PressureUnit.InchMercury -> when (target) {
            PressureUnit.HectoPascal -> value / HPA_TO_INCH_MERCURY
            PressureUnit.InchMercury -> value
        }
    }
