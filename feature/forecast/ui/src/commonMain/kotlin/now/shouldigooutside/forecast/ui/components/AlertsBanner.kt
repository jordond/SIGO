package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.alerts_banner_count_one
import now.shouldigooutside.core.resources.alerts_banner_count_other
import now.shouldigooutside.core.resources.alerts_banner_icon_cd
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.TriangleAlert
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun AlertsBanner(
    alerts: List<Alert>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (alerts.isEmpty()) return

    val countText = if (alerts.size == 1) {
        Res.string.alerts_banner_count_one.get()
    } else {
        Res.string.alerts_banner_count_other.get(alerts.size)
    }
    val first = alerts.first()
    val summary = first.headline ?: first.title

    Card(
        onClick = onClick,
        colors = CardDefaults.primaryColors,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(
                vertical = AppTheme.spacing.small,
                horizontal = AppTheme.spacing.standard,
            ),
        ) {
            Icon(
                icon = AppIcons.Lucide.TriangleAlert,
                contentDescription = Res.string.alerts_banner_icon_cd.get(),
            )
            Column {
                Text(
                    text = countText,
                    style = AppTheme.typography.label2,
                )
                Text(
                    text = summary,
                    style = AppTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private class AlertsBannerParams : PreviewParameterProvider<List<Alert>> {
    override val values: Sequence<List<Alert>> = sequenceOf(
        listOf(
            Alert(
                title = "Flood risk",
                description = "...",
                headline = "yellow warning - rainfall - in effect",
            ),
        ),
        listOf(
            Alert(title = "Flood risk", description = "..."),
            Alert(title = "Wind advisory", description = "..."),
        ),
        listOf(
            Alert(title = "Flood risk", description = "..."),
            Alert(title = "Wind advisory", description = "..."),
            Alert(title = "Heat advisory", description = "..."),
        ),
    )
}

@Preview
@Composable
private fun AlertsBannerPreview(
    @PreviewParameter(AlertsBannerParams::class) alerts: List<Alert>,
) {
    AppPreview {
        AlertsBanner(alerts = alerts, onClick = {})
    }
}
