package now.shouldigooutside.ui.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import now.shouldigooutside.ui.home.navigation.HomeTab

@Stable
internal class HomeModel : StateViewModel<HomeModel.State>(State()) {
    fun updateSelectedTab(tab: HomeTab) {
        updateState { it.copy(selectedTab = tab) }
    }

    @Immutable
    data class State(
        val selectedTab: HomeTab = HomeTab.Forecast,
    )
}
