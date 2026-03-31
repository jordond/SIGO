package now.shouldigooutside.forecast.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.dispatcher.rememberRelayOf
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.activities_empty_button
import now.shouldigooutside.core.resources.activities_empty_description
import now.shouldigooutside.core.resources.activities_empty_title
import now.shouldigooutside.core.resources.home_tab_activities
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.TabHeader
import now.shouldigooutside.core.ui.activities.key
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.forecast.ui.components.PeriodSelector
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ActivitiesTab(
    toSettings: () -> Unit,
    toAddActivity: () -> Unit,
    model: ActivitiesModel = koinViewModel(),
) {
    val state by model.collectAsState()

    ActivitiesTab(
        period = state.period,
        activities = state.scores,
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is ActivitiesTabAction.ActivityClick -> {}
                is ActivitiesTabAction.ChangePeriod -> {
                    model.update(action.period)
                }
                is ActivitiesTabAction.ToSettings -> {
                    toSettings()
                }
                is ActivitiesTabAction.ToAddActivity -> {
                    toAddActivity()
                }
            }
        },
    )
}

@Composable
internal fun ActivitiesTab(
    period: ForecastPeriod,
    activities: PersistentList<ActivityForecastScore>,
    modifier: Modifier = Modifier,
    dispatcher: Dispatcher<ActivitiesTabAction> = rememberDispatcher {},
    listState: LazyListState = rememberLazyListState(),
) {
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
                ElevatedCard(
                    colors = AppTheme.colors.brutal.green
                        .cardColors(),
                    modifier = Modifier.animateItem(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
                        modifier = Modifier
                            .padding(
                                vertical = AppTheme.spacing.large,
                                horizontal = AppTheme.spacing.standard,
                            ).fillMaxWidth(),
                    ) {
                        Text(
                            Res.string.activities_empty_title,
                            style = AppTheme.typography.h2,
                            textAlign = TextAlign.Center,
                        )

                        Text(
                            Res.string.activities_empty_description,
                            style = AppTheme.typography.body1,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier)

                        Button(
                            text = Res.string.activities_empty_button.get(),
                            variant = ButtonVariant.PrimaryElevated,
                            textStyle = AppTheme.typography.h2,
                            onClick = dispatcher.rememberRelay(ActivitiesTabAction.ToAddActivity),
                            modifier = Modifier.height(70.dp),
                        )
                    }
                }
            }
        } else {
            items(activities, key = { it.activity.key() }) { score ->
                ActivityScoreCard(
                    period = period,
                    data = score,
                    onClick = { dispatcher.dispatch(ActivitiesTabAction.ActivityClick(score.activity)) },
                    modifier = Modifier.animateItem(),
                )
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
    AppPreview {
        ActivitiesTab(
            period = ForecastPeriod.Now,
            activities = PreviewData.activityScores { index, _ ->
                when (index) {
                    0 -> PreviewData.Score.yes
                    1 -> PreviewData.Score.maybe
                    2 -> PreviewData.Score.no
                    else -> PreviewData.Score.yes
                }
            },
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
