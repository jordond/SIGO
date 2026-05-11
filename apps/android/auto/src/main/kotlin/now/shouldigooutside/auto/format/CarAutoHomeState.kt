package now.shouldigooutside.auto.format

import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.Units

internal data class CarAutoHomeState(
    val status: AsyncResult<Forecast>,
    val units: Units,
    val selectedActivity: Activity,
    val currentScore: ScoreResult?,
    val locationName: String?,
)
