package app.sigot.core.ui.mappers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.ktx.text
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.blank
import now.shouldigooutside.core.resources.forecast_period_hour
import now.shouldigooutside.core.resources.forecast_period_now
import now.shouldigooutside.core.resources.forecast_period_today
import now.shouldigooutside.core.resources.forecast_period_tomorrow
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

private val hours = listOf(
    ForecastPeriod.NextHour,
    ForecastPeriod.NextHour2,
    ForecastPeriod.NextHour3,
)

@Composable
public fun ForecastPeriod.rememberText(currentTime: Instant): String {
    if (this in hours) {
        val newTime = remember(this, currentTime) {
            val addHours = hours.indexOf(this) + 1
            val time = currentTime
                .plus(addHours.hours)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .time

            LocalTime(time.hour, 0)
        }

        return Res.string.forecast_period_hour.get(newTime.text())
    } else {
        val resource = remember(this) {
            when (this) {
                ForecastPeriod.Today -> Res.string.forecast_period_today
                ForecastPeriod.Now -> Res.string.forecast_period_now
                ForecastPeriod.Tomorrow -> Res.string.forecast_period_tomorrow
                else -> Res.string.blank // Shouldn't ever get here
            }
        }
        return resource.get()
    }
}
