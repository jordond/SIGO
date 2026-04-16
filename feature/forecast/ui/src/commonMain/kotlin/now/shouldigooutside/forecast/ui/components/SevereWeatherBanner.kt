package now.shouldigooutside.forecast.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.score_severe_weather_near
import now.shouldigooutside.core.resources.score_severe_weather_outside
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.OctagonAlert
import now.shouldigooutside.core.ui.icons.lucide.TriangleAlert
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun SevereWeatherBanner(
    severity: Severity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = severity != null,
        modifier = modifier,
    ) {
        val resolved = severity ?: return@AnimatedVisibility
        val text = remember(resolved) {
            when (resolved) {
                Severity.Warning -> Res.string.score_severe_weather_near
                Severity.Danger -> Res.string.score_severe_weather_outside
            }
        }
        val colors = when (resolved) {
            Severity.Warning -> CardDefaults.primaryColors
            Severity.Danger -> CardDefaults.errorColors
        }
        val icon = remember(resolved) {
            when (resolved) {
                Severity.Warning -> AppIcons.Lucide.TriangleAlert
                Severity.Danger -> AppIcons.Lucide.OctagonAlert
            }
        }

        Card(
            onClick = onClick,
            colors = colors,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(
                    vertical = AppTheme.spacing.small,
                    horizontal = AppTheme.spacing.standard,
                ),
            ) {
                Icon(icon)
                Text(text = text.get())
            }
        }
    }
}

@Preview
@Composable
private fun SevereWeatherBannerWarningPreview() {
    AppPreview {
        SevereWeatherBanner(severity = Severity.Warning, onClick = {})
    }
}

@Preview
@Composable
private fun SevereWeatherBannerDangerPreview() {
    AppPreview {
        SevereWeatherBanner(severity = Severity.Danger, onClick = {})
    }
}
