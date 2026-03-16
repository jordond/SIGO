package now.shouldigooutside.core.model.score

public data class ForecastScore(
    val current: Score,
    val hours: List<Score>,
    val today: Score,
    val days: List<Score>,
)
