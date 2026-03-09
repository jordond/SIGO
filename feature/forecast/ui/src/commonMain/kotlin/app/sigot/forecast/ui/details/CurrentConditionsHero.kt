package app.sigot.forecast.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.units.Units
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.mappers.units.rememberUnit
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.ForecastPreviewData.ForecastBlockPreviewParameterProvider

@Composable
internal fun CurrentConditionsHero(
    block: ForecastBlock,
    today: ForecastBlock,
    units: Units,
    modifier: Modifier = Modifier,
) {
    val tempUnit = units.temperature.rememberUnit()

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = block.temperature.formatValue(tempUnit),
                style = AppTheme.typography.h1.copy(fontSize = 64.sp),
            )

            Text(
                text = block.temperature.formatFeelsLike(tempUnit),
                style = AppTheme.typography.body1,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = today.temperature.formatHigh(tempUnit),
                    style = AppTheme.typography.h3,
                )
                Text(
                    text = today.temperature.formatLow(tempUnit),
                    style = AppTheme.typography.h3,
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun CurrentConditionsHeroPreview(
    @PreviewParameter(ForecastBlockPreviewParameterProvider::class) block: ForecastBlock,
) {
    AppPreview {
        CurrentConditionsHero(
            block = block,
            today = block,
            units = Units.Metric,
            modifier = Modifier.padding(16.dp),
        )
    }
}
