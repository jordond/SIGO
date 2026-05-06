package now.shouldigooutside.baselineprofile.support

internal const val PACKAGE_NAME = "now.shouldigooutside"

// Mirror of now.shouldigooutside.core.ui.UiAutomatorTags. Kept duplicated
// to avoid pulling :core:ui (and Compose) into the benchmark test APK.
internal object Tags {
    const val HOME_CONTENT = "home_tab_content"
    const val HOME_FORECAST_LIST = "home_forecast_list"
    const val FORECAST_DETAIL_ENTRY = "forecast_detail_entry"

    fun bottomNav(name: String) = "bottom_nav_${name.lowercase()}"
}

// Order intentional: warms tab caches before returning to Home.
// Must remain a subset of HomeTab entry names (lowercased).
internal val BOTTOM_NAV_TABS = listOf("forecast", "activities", "preferences", "home")
