package app.sigot.core.config.model

public data class PrecipitationConfig(
    val maxChance: Float = Defaults.MAX_CHANCE,
    val lowAmountMm: Int = Defaults.LOW_AMOUNT_MM,
    val moderateAmountMm: Int = Defaults.MODERATE_AMOUNT_MM,
    val highAmountMm: Int = Defaults.HIGH_AMOUNT_MM,
) {
    internal constructor(
        maxChance: Float?,
        lowAmountMm: Int?,
        moderateAmountMm: Int?,
        highAmountMm: Int?,
    ) : this(
        maxChance = maxChance ?: Defaults.MAX_CHANCE,
        lowAmountMm = lowAmountMm ?: Defaults.LOW_AMOUNT_MM,
        moderateAmountMm = moderateAmountMm ?: Defaults.MODERATE_AMOUNT_MM,
        highAmountMm = highAmountMm ?: Defaults.HIGH_AMOUNT_MM,
    )

    internal companion object {
        internal object Defaults {
            const val MAX_CHANCE = 0.4f
            const val LOW_AMOUNT_MM = 2
            const val MODERATE_AMOUNT_MM = 5
            const val HIGH_AMOUNT_MM = 10
        }
    }
}
