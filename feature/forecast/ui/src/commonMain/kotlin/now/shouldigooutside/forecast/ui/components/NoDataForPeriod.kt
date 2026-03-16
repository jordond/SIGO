package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_period_no_data
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.TriangleAlert
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun NoDataForPeriod(modifier: Modifier = Modifier) {
    ElevatedCard(
        colors = CardDefaults.secondaryColors,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(AppTheme.spacing.standard)
                .widthIn(max = 300.dp),
        ) {
            Icon(AppIcons.Lucide.TriangleAlert, modifier = Modifier.size(32.dp))

            Text(
                text = Res.string.forecast_period_no_data,
                style = AppTheme.typography.h4,
            )
        }
    }
}

@Preview
@Composable
internal fun NoDataForPeriodPreview() {
    AppPreview {
        NoDataForPeriod(modifier = Modifier.padding(32.dp))
    }
}
