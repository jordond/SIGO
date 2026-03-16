package now.shouldigooutside.core.model.score

public data class Reasons(
    val wind: ReasonValue,
    val temperature: ReasonValue,
    val precipitation: ReasonValue,
    val severeWeather: ReasonValue,
)
