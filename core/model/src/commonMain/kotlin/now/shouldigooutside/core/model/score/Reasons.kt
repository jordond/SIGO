package now.shouldigooutside.core.model.score

import androidx.compose.runtime.Immutable

@Immutable
public data class Reasons(
    val wind: ReasonValue,
    val temperature: ReasonValue,
    val precipitation: ReasonValue,
    val severeWeather: ReasonValue,
)
