package now.shouldigooutside.ui.home.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.home_tab_activities
import now.shouldigooutside.core.resources.home_tab_forecast
import now.shouldigooutside.core.resources.home_tab_home
import now.shouldigooutside.core.resources.home_tab_preferences
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.CloudSun
import now.shouldigooutside.core.ui.icons.lucide.Grid2x2
import now.shouldigooutside.core.ui.icons.lucide.House
import now.shouldigooutside.core.ui.icons.lucide.SlidersHorizontal
import org.jetbrains.compose.resources.StringResource

public enum class HomeTab(
    public val title: StringResource,
    public val icon: ImageVector,
) {
    Home(
        title = Res.string.home_tab_home,
        icon = AppIcons.Lucide.House,
    ),
    Forecast(
        title = Res.string.home_tab_forecast,
        icon = AppIcons.Lucide.CloudSun,
    ),
    Activities(
        title = Res.string.home_tab_activities,
        icon = AppIcons.Lucide.Grid2x2,
    ),
    Preferences(
        title = Res.string.home_tab_preferences,
        icon = AppIcons.Lucide.SlidersHorizontal,
    ),
    ;

    public companion object {
        public val default: HomeTab = Home
    }
}
