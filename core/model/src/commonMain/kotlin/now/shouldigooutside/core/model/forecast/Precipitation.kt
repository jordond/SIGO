package now.shouldigooutside.core.model.forecast

/**
 * Represents a precipitation details.
 *
 * @property amount The amount of precipitation in base units.
 * @property probability The probability of precipitation occurrence, percent of 0=100.
 * @property types The types of precipitation.
 */
public data class Precipitation(
    val amount: Double,
    val probability: Int,
    val types: Set<PrecipitationType>,
) {
    val type: PrecipitationType? = types.firstOrNull()

    val isRain: Boolean = if (types.isEmpty()) {
        true
    } else {
        types.contains(PrecipitationType.Rain) || types.contains(PrecipitationType.FreezingRain)
    }
}

public enum class PrecipitationType {
    Rain,
    Snow,
    FreezingRain,
    Hail,
}
