package now.shouldigooutside.settings.ui.internal

import androidx.lifecycle.viewModelScope
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.forecast.ClearForecastUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.settings.InternalSettings
import now.shouldigooutside.core.model.settings.Settings

internal class InternalSettingsModel(
    private val settingsRepo: SettingsRepo,
    private val clearForecastUseCase: ClearForecastUseCase,
) : UiStateViewModel<InternalSettingsModel.State, InternalSettingsModel.Event>(
        State(settingsRepo.settings.value),
    ) {
    init {
        settingsRepo.settings.mergeState { state, value -> state.copy(settings = value) }
    }

    fun update(value: InternalSettings) {
        settingsRepo.update { settings ->
            settings.copy(internalSettings = value)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            clearForecastUseCase.clear()
        }
    }

    data class State(
        val settings: Settings,
    )

    sealed interface Event
}
