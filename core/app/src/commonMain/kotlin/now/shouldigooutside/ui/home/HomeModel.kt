package now.shouldigooutside.ui.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.mapDistinct
import now.shouldigooutside.ui.home.navigation.HomeTab

@Stable
internal class HomeModel(
    private val settingsRepo: SettingsRepo,
) : UiStateViewModel<HomeModel.State, HomeModel.Event>(State()) {
    init {
        viewModelScope.launch {
            settingsRepo.settings.mapDistinct { it.enableActivities }.collect { enableActivities ->
                if (!enableActivities && state.value.selectedTab == HomeTab.Activities) {
                    updateState { it.copy(selectedTab = HomeTab.Home) }
                    emit(Event.Navigate(HomeTab.Home))
                }
            }
        }
    }

    fun updateSelectedTab(tab: HomeTab) {
        updateState { it.copy(selectedTab = tab) }
        emit(Event.Navigate(tab))
    }

    @Immutable
    data class State(
        val selectedTab: HomeTab = HomeTab.default,
    )

    @Immutable
    sealed interface Event {
        data class Navigate(
            val tab: HomeTab,
        ) : Event
    }
}
