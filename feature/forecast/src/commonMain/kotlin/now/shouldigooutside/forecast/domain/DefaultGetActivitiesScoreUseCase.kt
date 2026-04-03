package now.shouldigooutside.forecast.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.mapDistinct
import now.shouldigooutside.core.model.getOrNull
import now.shouldigooutside.core.model.score.ActivityForecastScore
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
internal class DefaultGetActivitiesScoreUseCase(
    private val settingsRepo: SettingsRepo,
    private val forecastHolder: ForecastStateHolder,
    private val scoreCalculator: ScoreCalculator,
) : GetActivitiesScoreUseCase {
    override fun scores(): List<ActivityForecastScore> {
        val settings = settingsRepo.settings.value
        val forecast = forecastHolder.state.value.getOrNull() ?: return emptyList()
        return settings.activities.map { (activity, preference) ->
            val score = scoreCalculator.calculate(forecast, preference, settings.includeAirQuality)
            ActivityForecastScore(
                activity = activity,
                preferences = preference,
                score = score,
            )
        }
    }

    override fun scoresFlow(): Flow<List<ActivityForecastScore>> =
        combine(
            settingsRepo.settings
                .mapDistinct { it.activities to it.includeAirQuality }
                .debounce(500.milliseconds),
            forecastHolder.state.mapNotNull { it.getOrNull() },
        ) { (activities, includeAirQuality), forecast ->
            activities.map { (activity, preference) ->
                val score = scoreCalculator.calculate(forecast, preference, includeAirQuality)
                ActivityForecastScore(
                    activity = activity,
                    preferences = preference,
                    score = score,
                )
            }
        }.distinctUntilChanged().flowOn(Dispatchers.Default)
}
