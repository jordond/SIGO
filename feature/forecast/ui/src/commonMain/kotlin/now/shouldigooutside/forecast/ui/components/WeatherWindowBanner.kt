package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import now.shouldigooutside.core.model.forecast.WeatherWindow
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.weather_window
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Sun
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.ktx.text

@Composable
internal fun WeatherWindowBanner(
    window: WeatherWindow,
    modifier: Modifier = Modifier,
) {
    val startTime = remember(window.start) {
        window.start.toLocalDateTime(TimeZone.currentSystemDefault()).time
    }
    val endTime = remember(window.end) {
        window.end.toLocalDateTime(TimeZone.currentSystemDefault()).time
    }

    val startText = startTime.text()
    val endText = endTime.text()
    val text = Res.string.weather_window.get(startText, endText)

    Card(
        colors = CardDefaults.primaryColors,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
            modifier = Modifier.padding(
                vertical = AppTheme.spacing.small,
                horizontal = AppTheme.spacing.standard,
            ),
        ) {
            Icon(AppIcons.Lucide.Sun)
            Text(
                text = text,
                style = AppTheme.typography.h4,
            )
        }
    }
}
