package now.shouldigooutside.settings.ui.preferences

import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.model.units.convertTemperature
import now.shouldigooutside.core.model.units.convertWindSpeed

private const val DEFAULT_MIN_TEMP_RANGE = -30.0
private const val DEFAULT_MAX_TEMP_RANGE = 40.0
private const val DEFAULT_MAX_WIND_SPEED = 50.0

internal class PreferencesModel(
    private val settingsRepo: SettingsRepo,
) : StateViewModel<PreferencesModel.State>(State(settingsRepo.settings.value.preferences)) {
    init {
        settingsRepo.settings
            .map { it.preferences }
            .distinctUntilChanged()
            .mergeState { state, preferences -> state.copy(preferences = preferences) }
    }

    fun update(preferences: Preferences) {
        settingsRepo.update { settings ->
            settings.updatePreferences(preferences)
        }
    }

    data class State(
        val preferences: Preferences,
        val rangeTempStart: Double = DEFAULT_MIN_TEMP_RANGE,
        val rangeTempEnd: Double = DEFAULT_MAX_TEMP_RANGE,
        val rangeWindSpeed: Double = DEFAULT_MAX_WIND_SPEED,
    ) {
        private val startTempRange =
            convertTemperature(
                value = rangeTempStart,
                from = TemperatureUnit.Celsius,
                target = preferences.units.temperature,
            ).toFloat()

        private val endTempRange =
            convertTemperature(
                value = rangeTempEnd,
                from = TemperatureUnit.Celsius,
                target = preferences.units.temperature,
            ).toFloat()

        val tempRange = startTempRange..endTempRange

        val maxWindSpeed = convertWindSpeed(
            value = rangeWindSpeed,
            from = WindSpeedUnit.KilometerPerHour,
            target = preferences.units.windSpeed,
        ).toFloat()
    }
}
