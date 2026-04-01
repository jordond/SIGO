package now.shouldigooutside.forecast.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.dispatcher.rememberRelayOf
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.forecast.blockForPeriod
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.home_tab_activities
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.TabHeader
import now.shouldigooutside.core.ui.activities.key
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.forecast.ui.activities.ActivitiesModel.Event
import now.shouldigooutside.forecast.ui.activities.components.ActivityScoreCard
import now.shouldigooutside.forecast.ui.activities.components.AddActivityCard
import now.shouldigooutside.forecast.ui.activities.components.NoActivitiesCard
import now.shouldigooutside.forecast.ui.components.PeriodSelector
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ActivitiesTab(
    toSettings: () -> Unit,
    toAddActivity: () -> Unit,
    toHome: () -> Unit,
    model: ActivitiesModel = koinViewModel(),
) {
    val state by model.collectAsState()

    HandleEvents(model) { events ->
        when (events) {
            is Event.ToHome -> toHome()
        }
    }

    ActivitiesTab(
        period = state.period,
        activities = state.scores,
        forecast = state.forecast,
        canAdd = state.canAddMore,
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is ActivitiesTabAction.ActivityClick -> model.activityCardClick(action.activity)
                is ActivitiesTabAction.ChangePeriod -> model.update(action.period)
                is ActivitiesTabAction.ToSettings -> toSettings()
                is ActivitiesTabAction.ToAddActivity -> toAddActivity()
            }
        },
    )
}

@Composable
internal fun ActivitiesTab(
    period: ForecastPeriod,
    activities: PersistentList<ActivityForecastScore>,
    modifier: Modifier = Modifier,
    canAdd: Boolean = true,
    forecast: Forecast? = null,
    dispatcher: Dispatcher<ActivitiesTabAction> = rememberDispatcher {},
    listState: LazyListState = rememberLazyListState(),
) {
    val block = remember(forecast, period) { forecast?.blockForPeriod(period) }
    val units = forecast?.units
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
        contentPadding = PaddingValues(
            horizontal = AppTheme.spacing.standard,
            vertical = AppTheme.spacing.standard,
        ),
        modifier = modifier.fillMaxSize(),
    ) {
        item(key = "header") {
            TabHeader(
                title = Res.string.home_tab_activities,
                toSettings = dispatcher.rememberRelay(ActivitiesTabAction.ToSettings),
            )
        }

        if (!activities.isEmpty()) {
            item(key = "period_selector") {
                PeriodSelector(
                    period = period,
                    changePeriod = dispatcher.rememberRelayOf(ActivitiesTabAction::ChangePeriod),
                    modifier = Modifier.animateItem(),
                )
            }
        }

        if (activities.isEmpty()) {
            item(key = "empty") {
                NoActivitiesCard(
                    onClick = dispatcher.rememberRelay(ActivitiesTabAction.ToAddActivity),
                    modifier = Modifier.animateItem(),
                )
            }
        } else {
            items(activities, key = { it.activity.key() }) { score ->
                ActivityScoreCard(
                    period = period,
                    data = score,
                    onClick = { dispatcher.dispatch(ActivitiesTabAction.ActivityClick(score.activity)) },
                    block = block,
                    units = units,
                    modifier = Modifier.animateItem(),
                )
            }

            if (canAdd) {
                item(key = "add_button") {
                    AddActivityCard(
                        onClick = dispatcher.rememberRelay(ActivitiesTabAction.ToAddActivity),
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }

        item(key = "spacer") {
            Spacer(Modifier.height(200.dp))
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
public fun ActivityTabPreview() {
    val forecast = PreviewData.Forecast.createForecast()
    AppPreview {
        ActivitiesTab(
            period = ForecastPeriod.Now,
            activities = PreviewData.activityScores(count = 2) { index, _ ->
                when (index) {
                    0 -> PreviewData.Score.yes
                    1 -> PreviewData.Score.maybe
                    2 -> PreviewData.Score.no
                    else -> PreviewData.Score.yes
                }
            },
            forecast = forecast,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun EmptyPreview() {
    AppPreview {
        ActivitiesTab(
            period = ForecastPeriod.Now,
            activities = persistentListOf(),
        )
    }
}
