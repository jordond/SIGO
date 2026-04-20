package now.shouldigooutside.widget

import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.ui.Colors
import now.shouldigooutside.core.ui.DarkColors
import now.shouldigooutside.core.ui.LightColors
import androidx.glance.color.ColorProvider as DayNightColorProvider

internal fun widgetColors(isDark: Boolean): Colors = if (isDark) DarkColors else LightColors

internal fun Colors.scoreColor(result: ScoreResult): Color =
    when (result) {
        ScoreResult.Yes -> success
        ScoreResult.Maybe -> primary
        ScoreResult.No -> error
    }

internal fun Color.toProvider(): ColorProvider = DayNightColorProvider(this, this)

internal val BlackProvider: ColorProvider = Color.Black.toProvider()
internal val WhiteProvider: ColorProvider = Color.White.toProvider()
internal val BlackLocationProvider: ColorProvider =
    Color.Black.copy(alpha = WidgetDimens.LOCATION_ALPHA).toProvider()
internal val BlackActivityProvider: ColorProvider =
    Color.Black.copy(alpha = WidgetDimens.ACTIVITY_ALPHA).toProvider()
internal val BlackStaleProvider: ColorProvider =
    Color.Black.copy(alpha = WidgetDimens.STALE_ALPHA).toProvider()
