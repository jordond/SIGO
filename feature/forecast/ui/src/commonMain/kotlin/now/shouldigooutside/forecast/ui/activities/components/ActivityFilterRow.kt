package now.shouldigooutside.forecast.ui.activities.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_title_in
import now.shouldigooutside.core.resources.when_text
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.asDisplay
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.forecast.ui.components.PeriodSelector

@Composable
internal fun ActivityFilterRow(
    period: ForecastPeriod,
    changePeriod: (ForecastPeriod) -> Unit,
    modifier: Modifier = Modifier,
    location: Location? = null,
    onLocationClick: () -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        itemVerticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = Res.string.when_text,
            fontStyle = FontStyle.Italic,
            style = AppTheme.typography.h2,
        )

        Spacer(Modifier.width(AppTheme.spacing.small))

        PeriodSelector(
            period = period,
            changePeriod = changePeriod,
        )

        if (location != null) {
            Spacer(Modifier.width(AppTheme.spacing.small))

            Text(
                text = Res.string.forecast_title_in.get(),
                fontStyle = FontStyle.Italic,
                style = AppTheme.typography.h2,
            )

            Spacer(Modifier.width(AppTheme.spacing.small))

            Button(
                onClick = onLocationClick,
                shape = AppTheme.shapes.extraSmall,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                variant = ButtonVariant.SecondaryElevated,
            ) {
                Text(
                    text = location.name,
                    style = AppTheme.typography.h3.asDisplay,
                    autoSize = AppTheme.typography.h3.autoSize(),
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun PeriodOnlyPreview() {
    AppPreview {
        ActivityFilterRow(
            period = ForecastPeriod.Now,
            changePeriod = {},
            onLocationClick = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun WithLocationPreview() {
    AppPreview {
        ActivityFilterRow(
            period = ForecastPeriod.Today,
            changePeriod = {},
            location = Location(0.0, 0.0, "Toronto"),
            onLocationClick = {},
        )
    }
}
