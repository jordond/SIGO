package now.shouldigooutside.settings.ui.preferences.tab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.dispatcher.rememberRelayOf
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.preferences.remainingActivities
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.are_you_sure
import now.shouldigooutside.core.resources.delete
import now.shouldigooutside.core.resources.delete_activity_message
import now.shouldigooutside.core.resources.preferences
import now.shouldigooutside.core.resources.reset
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.TabHeader
import now.shouldigooutside.core.ui.activities.ActivitySelector
import now.shouldigooutside.core.ui.components.AlertDialog
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.IconButton
import now.shouldigooutside.core.ui.components.IconButtonVariant
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.RotateCcw
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preferences.PreferencesList
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.settings.ui.navigation.PreferencesTabRoute
import now.shouldigooutside.settings.ui.preferences.PreferencesModel
import org.koin.compose.viewmodel.koinViewModel

public fun NavGraphBuilder.preferencesTab(
    toAddActivity: () -> Unit,
    toSettings: () -> Unit,
) {
    composable<PreferencesTabRoute> {
        PreferencesTab(toAddActivity = toAddActivity, toSettings = toSettings)
    }
}

@Composable
internal fun PreferencesTab(
    toAddActivity: () -> Unit,
    toSettings: () -> Unit,
    model: PreferencesModel = koinViewModel(),
) {
    val state by model.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    PreferencesTab(
        selected = state.selected,
        activities = state.activities,
        units = state.units,
        selectedPreferences = state.preferences,
        temperatureRange = state.tempRange,
        maxWindSpeed = state.maxWindSpeed,
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is PreferencesAction.Select -> model.selectActivity(action.activity)
                is PreferencesAction.ToAddActivity -> toAddActivity()
                is PreferencesAction.ToSettings -> toSettings()
                is PreferencesAction.Update -> model.update(action.preferences)
                is PreferencesAction.Delete -> showDeleteDialog = true
                is PreferencesAction.ResetPreferences -> model.resetSelectedPreferences()
            }
        },
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            onConfirmClick = { model.deleteSelectedActivity() },
            title = Res.string.are_you_sure.get(),
            text = Res.string.delete_activity_message.get(),
            confirmButtonText = Res.string.delete.get(),
        )
    }
}

@Composable
internal fun PreferencesTab(
    selected: Activity,
    selectedPreferences: Preferences,
    activities: PersistentMap<Activity, Preferences>,
    units: Units,
    dispatcher: Dispatcher<PreferencesAction>,
    modifier: Modifier = Modifier,
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        TabHeader(
            title = Res.string.preferences,
            toSettings = dispatcher.rememberRelay(PreferencesAction.ToSettings),
        )

        val canAdd = remember(activities) { activities.remainingActivities().isNotEmpty() }
        val activityList = remember(activities) { activities.keys.toPersistentList() }
        ActivitySelector(
            selected = selected,
            onSelected = dispatcher.rememberRelayOf(PreferencesAction::Select),
            onAddCustom = dispatcher.rememberRelay(PreferencesAction.ToAddActivity),
            activities = activityList,
            canAdd = canAdd,
            contentPadding = PaddingValues(16.dp),
        )

        Spacer(Modifier.height(8.dp))

        PreferencesList(
            units = units,
            preferences = selectedPreferences,
            updatePreferences = dispatcher.rememberRelayOf(PreferencesAction::Update),
            temperatureRange = temperatureRange,
            maxWindSpeed = maxWindSpeed,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        )

        AnimatedVisibility(visible = selected != Activity.General) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 400.dp)
                    .height(65.dp),
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    Button(
                        onClick = dispatcher.rememberRelay(PreferencesAction.Delete),
                        variant = ButtonVariant.DestructiveElevated,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(Res.string.delete, style = AppTheme.typography.h3)
                    }
                }

                IconButton(
                    variant = IconButtonVariant.SecondaryElevated,
                    onClick = dispatcher.rememberRelay(PreferencesAction.ResetPreferences),
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                ) {
                    Icon(
                        icon = AppIcons.Lucide.RotateCcw,
                        contentDescription = Res.string.reset.get(),
                    )
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
public fun PreferencesTabPreview() {
    var preferences by remember { mutableStateOf(Preferences.default) }
    AppPreview {
        PreferencesTab(
            selected = Activity.Running,
            selectedPreferences = preferences,
            activities = PreviewData.activities(2),
            units = Units.Metric,
            dispatcher = rememberDispatcher { },
        )
    }
}
