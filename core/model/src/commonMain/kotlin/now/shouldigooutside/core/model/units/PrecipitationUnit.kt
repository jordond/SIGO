package now.shouldigooutside.core.model.units

public enum class PrecipitationUnit(
    override val label: String,
) : BaseUnit {
    Millimeter("mm"),
    Inch("in"),
    ;

    public companion object
}
