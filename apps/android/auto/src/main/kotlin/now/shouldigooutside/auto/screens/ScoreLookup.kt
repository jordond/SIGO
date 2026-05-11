package now.shouldigooutside.auto.screens

import kotlinx.collections.immutable.PersistentList
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.ForecastScore

internal fun PersistentList<ActivityForecastScore>.forecastScoreFor(activity: Activity): ForecastScore? =
    firstOrNull { it.activity == activity }?.score
