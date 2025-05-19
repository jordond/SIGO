package app.sigot.onboarding.ui.preferences

import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.model.units.WindSpeedUnit
import app.sigot.core.model.units.convertTemperature
import app.sigot.core.model.units.convertWindSpeed
import app.sigot.onboarding.ui.preferences.PreferencesModel.Event
import app.sigot.onboarding.ui.preferences.PreferencesModel.State
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

// TODO: Get these values from remote config
private const val DEFAULT_MIN_TEMP_RANGE = -30.0
private const val DEFAULT_MAX_TEMP_RANGE = 40.0
private const val DEFAULT_MAX_WIND_SPEED = 50.0

internal class PreferencesModel(
    private val settingsRepo: SettingsRepo,
) : UiStateViewModel<State, Event>(State(settingsRepo.settings.value.preferences)) {
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

    sealed interface Event
}
