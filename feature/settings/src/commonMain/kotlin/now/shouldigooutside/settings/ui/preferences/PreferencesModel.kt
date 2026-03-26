package now.shouldigooutside.settings.ui.preferences

import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.collections.immutable.PersistentMap
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.model.units.convertTemperature
import now.shouldigooutside.core.model.units.convertWindSpeed

private const val DEFAULT_MIN_TEMP_RANGE = -30.0
private const val DEFAULT_MAX_TEMP_RANGE = 40.0
private const val DEFAULT_MAX_WIND_SPEED = 50.0

@Stable
internal class PreferencesModel(
    private val settingsRepo: SettingsRepo,
) : StateViewModel<PreferencesModel.State>(
        State(
            activities = settingsRepo.settings.value.activities,
            selected = settingsRepo.settings.value.selectedActivity,
            units = settingsRepo.settings.value.units,
        ),
    ) {
    init {
        settingsRepo.settings
            .map { Triple(it.activities, it.selectedActivity, it.units) }
            .distinctUntilChanged()
            .mergeState { state, (activities, selected, units) ->
                state.copy(activities = activities, selected = selected, units = units)
            }
    }

    fun selectActivity(activity: Activity) {
        settingsRepo.update { settings ->
            settings.copy(selectedActivity = activity)
        }
    }

    fun update(preferences: Preferences) {
        settingsRepo.update { settings ->
            settings.updatePreferences(Activity.General, preferences)
        }
    }

    data class State(
        val activities: PersistentMap<Activity, Preferences>,
        val selected: Activity,
        val units: Units,
        val rangeTempStart: Double = DEFAULT_MIN_TEMP_RANGE,
        val rangeTempEnd: Double = DEFAULT_MAX_TEMP_RANGE,
        val rangeWindSpeed: Double = DEFAULT_MAX_WIND_SPEED,
    ) {
        private val startTempRange =
            convertTemperature(
                value = rangeTempStart,
                from = TemperatureUnit.Celsius,
                target = units.temperature,
            ).toFloat()

        private val endTempRange =
            convertTemperature(
                value = rangeTempEnd,
                from = TemperatureUnit.Celsius,
                target = units.temperature,
            ).toFloat()

        val tempRange = startTempRange..endTempRange

        val maxWindSpeed = convertWindSpeed(
            value = rangeWindSpeed,
            from = WindSpeedUnit.KilometerPerHour,
            target = units.windSpeed,
        ).toFloat()

        val preferences: Preferences = activities[selected] ?: Preferences.defaultFor(selected)
    }
}
