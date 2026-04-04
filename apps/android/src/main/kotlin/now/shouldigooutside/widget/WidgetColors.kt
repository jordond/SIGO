package now.shouldigooutside.widget

import androidx.compose.ui.graphics.Color
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.ui.Colors
import now.shouldigooutside.core.ui.DarkColors
import now.shouldigooutside.core.ui.LightColors

internal fun widgetColors(isDark: Boolean): Colors = if (isDark) DarkColors else LightColors

internal fun Colors.scoreColor(result: ScoreResult): Color =
    when (result) {
        ScoreResult.Yes -> success
        ScoreResult.Maybe -> primary
        ScoreResult.No -> error
    }
