package app.sigot.forecast.ui.components.mappers

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import app.sigot.core.model.ForecastPeriodData
import app.sigot.core.model.score.ScoreResult
import app.sigot.core.resources.Res
import app.sigot.core.resources.score_maybe
import app.sigot.core.resources.score_no
import app.sigot.core.resources.score_yes
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.BrutalColors
import app.sigot.core.ui.brutal
import app.sigot.core.ui.ktx.get

@Composable
internal fun ForecastPeriodData.brutalColor(): BrutalColors =
    when (score.result) {
        ScoreResult.Yes -> AppTheme.colors.brutal.green
        ScoreResult.Maybe -> AppTheme.colors.brutal.yellow
        ScoreResult.No -> AppTheme.colors.brutal.red
    }

@Composable
internal fun ForecastPeriodData.colors(): Pair<Color, Color> {
    val colors = brutalColor()
    val containerColor by animateColorAsState(colors.container)
    val contentColor = colors.containerContent

    return containerColor to contentColor
}

@Composable
internal fun ForecastPeriodData.rememberScoreText(): String {
    val res = remember(score.result) {
        when (score.result) {
            ScoreResult.Yes -> Res.string.score_yes
            ScoreResult.Maybe -> Res.string.score_maybe
            ScoreResult.No -> Res.string.score_no
        }
    }

    return res.get()
}
