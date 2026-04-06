package now.shouldigooutside.core.model.units

public enum class TemperatureUnit(
    override val label: String,
) : BaseUnit {
    Kelvin("K"),
    Celsius("°C"),
    Fahrenheit("°F"),
    ;

    public companion object
}
