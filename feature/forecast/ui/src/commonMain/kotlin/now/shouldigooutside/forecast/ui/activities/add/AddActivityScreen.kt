package now.shouldigooutside.forecast.ui.activities.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.PreferenceRanges
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.activity_add_preferences_description
import now.shouldigooutside.core.resources.activity_add_title
import now.shouldigooutside.core.resources.reset
import now.shouldigooutside.core.resources.save
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.activities.key
import now.shouldigooutside.core.ui.activities.rememberStringResource
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Scaffold
import now.shouldigooutside.core.ui.components.ScaffoldScope.innerPadding
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.topbar.TopBar
import now.shouldigooutside.core.ui.components.topbar.TopBarDefaults
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preferences.PreferencesList
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.forecast.ui.activities.add.AddActivityModel.Event
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AddActivityScreen(
    onBack: () -> Unit,
    model: AddActivityModel = koinViewModel(),
) {
    val state by model.collectAsState()

    HandleEvents(model) { event ->
        when (event) {
            is Event.Finished -> onBack()
        }
    }

    AddActivityScreen(
        activities = state.activities,
        activity = state.activity,
        preferences = state.preferences,
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is AddActivityAction.Cancel -> onBack()
                is AddActivityAction.Select -> model.select(action.activity)
                is AddActivityAction.Update -> model.updatePreferences(action.preferences)
                is AddActivityAction.ResetPreferences -> model.resetPreferences()
                is AddActivityAction.Save -> model.save()
            }
        },
    )
}

@Composable
internal fun AddActivityScreen(
    activities: PersistentList<Activity>,
    activity: Activity?,
    preferences: Preferences,
    modifier: Modifier = Modifier,
    units: Units = Units.Metric,
    ranges: PreferenceRanges = PreferenceRanges.from(Units.Metric),
    dispatcher: Dispatcher<AddActivityAction> = rememberDispatcher { },
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    val scrollBehavior = TopBarDefaults.enterAlwaysScrollBehavior()
    val list = remember(activities) { activities + Activity.Custom("") }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Title(Res.string.activity_add_title.get())
                },
                navigationIcon = {
                    CloseButton(onClick = dispatcher.rememberRelay(AddActivityAction.Cancel))
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = activity != null,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it + (it / 2) },
            ) {
                Button(
                    variant = ButtonVariant.PrimaryElevated,
                    onClick = dispatcher.rememberRelay(AddActivityAction.Save),
                    modifier = Modifier.height(75.dp).width(150.dp),
                ) {
                    Text(Res.string.save, style = AppTheme.typography.h2)
                }
            }
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 150.dp),
            modifier = Modifier
                .innerPadding(innerPadding, bottom = false)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            items(
                items = list,
                key = { it.key() },
                contentType = { it },
            ) { item ->
                val visible = activity == null
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.8f),
                    exit = fadeOut(tween(180)) + scaleOut(tween(180), targetScale = 0.8f),
                    modifier = Modifier.animateItem(
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy,
                        ),
                    ),
                ) {
                    AddActivityItem(
                        activity = item,
                        selected = false,
                        onClick = { dispatcher.dispatch(AddActivityAction.Select(item)) },
                        modifier = Modifier.aspectRatio(1f),
                    )
                }
            }

            item(key = "preferences", span = { GridItemSpan(maxLineSpan) }) {
                AnimatedVisibility(
                    visible = activity != null,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 },
                    exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { it / 2 },
                    modifier = Modifier.animateItem(),
                ) {
                    if (activity != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            AddActivityItem(
                                activity = activity,
                                selected = true,
                                onClick = { dispatcher.dispatch(AddActivityAction.Select(activity)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                            )

                            val title = activity.rememberStringResource().get()
                            Text(Res.string.activity_add_preferences_description.get(title))

                            PreferencesList(
                                units = units,
                                preferences = preferences,
                                updatePreferences = dispatcher.rememberRelayOf(AddActivityAction::Update),
                                temperatureRange = ranges.temperature,
                                maxWindSpeed = ranges.maxWindSpeed,
                            )

                            Button(
                                variant = ButtonVariant.Outlined,
                                onClick = dispatcher.rememberRelay(AddActivityAction.ResetPreferences),
                            ) {
                                Text(Res.string.reset)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        AddActivityScreen(
            activities = Activity.all.minus(Activity.General).toPersistentList(),
            activity = null,
            preferences = Preferences.default,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Selected() {
    AppPreview {
        AddActivityScreen(
            activities = Activity.all.minus(Activity.General).toPersistentList(),
            activity = Activity.Running,
            preferences = Preferences.default,
        )
    }
}
