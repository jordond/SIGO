package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.alerts_sheet_active_range
import now.shouldigooutside.core.resources.alerts_sheet_empty_body
import now.shouldigooutside.core.resources.alerts_sheet_more_info
import now.shouldigooutside.core.resources.alerts_sheet_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.TriangleAlert
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.ktx.rememberTimeOfDay
import now.shouldigooutside.core.ui.preview.AppPreview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant

@Composable
internal fun AlertsBottomSheet(model: AlertsBottomSheetModel = koinViewModel()) {
    val state by model.collectAsState()
    AlertsBottomSheet(alerts = state.alerts)
}

@Composable
internal fun AlertsBottomSheet(
    alerts: List<Alert>,
    modifier: Modifier = Modifier,
) {
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
            Icon(icon = AppIcons.Lucide.TriangleAlert)
            Text(
                text = Res.string.alerts_sheet_title.get(),
                style = AppTheme.typography.h3,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (alerts.isEmpty()) {
            Text(
                text = Res.string.alerts_sheet_empty_body.get(),
                style = AppTheme.typography.body1,
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(alerts) { alert ->
                    AlertCard(alert = alert)
                }
            }
        }
    }
}

@Composable
private fun AlertCard(
    alert: Alert,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val onset = alert.onset
    val ends = alert.ends
    val rangeText = if (onset != null && ends != null) {
        Res.string.alerts_sheet_active_range.get(
            onset.rememberTimeOfDay(),
            ends.rememberTimeOfDay(),
        )
    } else {
        null
    }

    Card(
        colors = CardDefaults.primaryColors,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(AppTheme.spacing.standard),
        ) {
            Text(
                text = alert.headline ?: alert.title,
                style = AppTheme.typography.label1,
            )

            rangeText?.let { range ->
                Text(
                    text = range,
                    style = AppTheme.typography.label3,
                )
            }

            val paragraphs = remember(alert.description) { alert.descriptionParagraphs }
            paragraphs.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = AppTheme.typography.body2,
                )
            }

            alert.link?.takeIf { it.isNotBlank() }?.let { link ->
                val moreInfoText = Res.string.alerts_sheet_more_info.get()
                Button(
                    text = moreInfoText,
                    onClick = { uriHandler.openUri(link) },
                )
            }
        }
    }
}

private class AlertsSheetParams : PreviewParameterProvider<List<Alert>> {
    override val values: Sequence<List<Alert>> = sequenceOf(
        emptyList(),
        listOf(
            Alert(
                title = "Flood risk",
                description = "Heavy rainfall expected.### Stay indoors.",
                headline = "yellow warning - rainfall - in effect",
                onset = Instant.fromEpochSeconds(1_700_000_000L),
                ends = Instant.fromEpochSeconds(1_700_010_000L),
                link = "https://example.com/alert/1",
            ),
        ),
        listOf(
            Alert(
                title = "Flood risk",
                description = "Heavy rainfall expected.### Stay indoors.",
                headline = "yellow warning - rainfall - in effect",
            ),
            Alert(
                title = "Wind advisory",
                description = "Gusts 60+ km/h.",
            ),
        ),
    )
}

@Preview
@Composable
private fun AlertsBottomSheetPreview(
    @PreviewParameter(AlertsSheetParams::class) alerts: List<Alert>,
) {
    AppPreview {
        AlertsBottomSheet(alerts = alerts)
    }
}
