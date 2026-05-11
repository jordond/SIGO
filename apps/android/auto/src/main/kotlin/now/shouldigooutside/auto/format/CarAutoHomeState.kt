package now.shouldigooutside.auto.format

import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.WeatherReason
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.Units

internal data class CarAutoHomeState(
    val status: AsyncResult<Forecast>,
    val units: Units,
    val selectedActivity: Activity,
    val currentScore: ScoreResult?,
    val currentReason: WeatherReason?,
    val locationName: String?,
)

internal data class HomePaneResult(
    val pane: Pane,
    val alertsAction: Action?,
)
