package now.shouldigooutside.core.widget

import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.widget_alerts
import now.shouldigooutside.core.resources.widget_empty
import now.shouldigooutside.core.resources.widget_feels_like
import now.shouldigooutside.core.resources.widget_precip
import now.shouldigooutside.core.resources.widget_today
import now.shouldigooutside.core.resources.widget_wind
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString

public data class WidgetStrings(
    val empty: String,
    val feelsLike: String,
    val wind: String,
    val precip: String,
    val today: String,
) {
    public companion object {
        public suspend fun resolve(): WidgetStrings =
            WidgetStrings(
                empty = getString(Res.string.widget_empty),
                feelsLike = getString(Res.string.widget_feels_like),
                wind = getString(Res.string.widget_wind),
                precip = getString(Res.string.widget_precip),
                today = getString(Res.string.widget_today),
            )
    }
}

public suspend fun WidgetData.resolveAlerts(): String? {
    if (alertCount <= 0) return null
    return getPluralString(Res.plurals.widget_alerts, alertCount, alertCount)
}
