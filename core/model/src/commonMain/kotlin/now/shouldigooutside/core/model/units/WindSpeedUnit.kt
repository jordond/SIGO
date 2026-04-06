package now.shouldigooutside.core.model.units

public enum class WindSpeedUnit(
    override val label: String,
) : BaseUnit {
    MeterPerSecond("m/s"),
    KilometerPerHour("km/h"),
    MilePerHour("mph"),
    Knot("kn"),
    ;

    public companion object
}
