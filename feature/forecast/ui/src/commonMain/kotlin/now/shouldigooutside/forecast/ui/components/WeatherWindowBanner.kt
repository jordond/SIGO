package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import now.shouldigooutside.core.model.forecast.WeatherWindow
import now.shouldigooutside.core.model.ui.AppExperience
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.weather_window_description
import now.shouldigooutside.core.resources.weather_window_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalAppExperience
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Sun
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.ktx.text
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData

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
    val title = Res.string.weather_window_title.get()
    val description = Res.string.weather_window_description.get(startText, endText)

    Card(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            modifier = Modifier.padding(AppTheme.spacing.standard),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(AppTheme.colors.onPrimary.copy(alpha = 0.15f)),
            ) {
                Icon(
                    AppIcons.Lucide.Sun,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = AppTheme.typography.h3,
                )
                Text(
                    text = description,
                    style = AppTheme.typography.body2,
                )
            }
        }
    }
}

@Preview
@Composable
private fun WeatherWindowBannerPreview() {
    val forecast = PreviewData.Forecast.createGoodWindowForecast()
    val goodWindow = PreviewData.Forecast.goodWindow(forecast) ?: return
    AppPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            modifier = Modifier.padding(AppTheme.spacing.standard),
        ) {
            WeatherWindowBanner(
                window = goodWindow,
                modifier = Modifier.fillMaxWidth(),
            )

            CompositionLocalProvider(
                LocalAppExperience provides AppExperience.default.copy(use24HourFormat = true),
            ) {
                WeatherWindowBanner(
                    window = goodWindow,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
