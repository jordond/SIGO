package now.shouldigooutside.core.model.units

public enum class PressureUnit(
    override val label: String,
) : BaseUnit {
    HectoPascal("hPa"),
    InchMercury("inHg"),
    ;

    public companion object
}
