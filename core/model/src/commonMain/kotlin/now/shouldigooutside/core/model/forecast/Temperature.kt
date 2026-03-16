package now.shouldigooutside.core.model.forecast

/**
 * Represents a temperature value for the forecast.
 *
 * @property value Value in base units.
 * @property feelsLike Feels like value in base units.
 * @property max Max value for the forecast block, if null, the [value] is used.
 * @property min Min value for the forecast block, if null, the [value] is used.
 */
public data class Temperature(
    val value: Double,
    val feelsLike: Double,
    val max: Double,
    val min: Double,
) {
    public constructor(
        value: Double,
        feelsLike: Double,
        max: Double?,
        min: Double?,
    ) : this(value, feelsLike, max ?: value, min ?: value)
}
