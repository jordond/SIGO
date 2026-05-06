package now.shouldigooutside.baselineprofile.support

internal const val PACKAGE_NAME = "now.shouldigooutside"

internal object Tags {
    const val HOME_CONTENT = "home_tab_content"
    const val HOME_FORECAST_LIST = "home_forecast_list"
    const val FORECAST_DETAIL_ENTRY = "forecast_detail_entry"

    fun bottomNav(tab: String) = "bottom_nav_${tab.lowercase()}"
}
