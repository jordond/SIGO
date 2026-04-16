package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.severe_weather_sheet_danger_body
import now.shouldigooutside.core.resources.severe_weather_sheet_danger_title
import now.shouldigooutside.core.resources.severe_weather_sheet_warning_body
import now.shouldigooutside.core.resources.severe_weather_sheet_warning_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.OctagonAlert
import now.shouldigooutside.core.ui.icons.lucide.TriangleAlert
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun SevereWeatherInfoBottomSheet(
    severity: Severity,
    modifier: Modifier = Modifier,
) {
    val title = remember(severity) {
        when (severity) {
            Severity.Warning -> Res.string.severe_weather_sheet_warning_title
            Severity.Danger -> Res.string.severe_weather_sheet_danger_title
        }
    }
    val body = remember(severity) {
        when (severity) {
            Severity.Warning -> Res.string.severe_weather_sheet_warning_body
            Severity.Danger -> Res.string.severe_weather_sheet_danger_body
        }
    }
    val icon = remember(severity) {
        when (severity) {
            Severity.Warning -> AppIcons.Lucide.TriangleAlert
            Severity.Danger -> AppIcons.Lucide.OctagonAlert
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.standard)
            .padding(bottom = AppTheme.spacing.standard),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon)
            Text(
                text = title.get(),
                style = AppTheme.typography.h3,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = body.get(),
            style = AppTheme.typography.body1,
        )
    }
}

@Preview
@Composable
private fun SevereWeatherInfoWarningPreview() {
    AppPreview {
        SevereWeatherInfoBottomSheet(severity = Severity.Warning)
    }
}

@Preview
@Composable
private fun SevereWeatherInfoDangerPreview() {
    AppPreview {
        SevereWeatherInfoBottomSheet(severity = Severity.Danger)
    }
}
