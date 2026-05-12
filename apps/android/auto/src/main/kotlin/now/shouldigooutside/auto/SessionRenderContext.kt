package now.shouldigooutside.auto

import now.shouldigooutside.auto.screens.AlertsTemplateBuilder
import now.shouldigooutside.auto.screens.HomeTemplateBuilder
import now.shouldigooutside.auto.screens.HourlyTemplateBuilder

internal data class SessionRenderContext(
    val homeBuilder: HomeTemplateBuilder,
    val hourlyBuilder: HourlyTemplateBuilder,
    val alertsBuilder: AlertsTemplateBuilder,
)
