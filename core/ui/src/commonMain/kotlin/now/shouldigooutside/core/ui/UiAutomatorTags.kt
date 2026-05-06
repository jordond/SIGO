package now.shouldigooutside.core.ui

public object UiAutomatorTags {
    public const val HOME_CONTENT: String = "home_tab_content"
    public const val HOME_FORECAST_LIST: String = "home_forecast_list"
    public const val FORECAST_DETAIL_ENTRY: String = "forecast_detail_entry"

    public fun bottomNav(name: String): String = "bottom_nav_${name.lowercase()}"
}
