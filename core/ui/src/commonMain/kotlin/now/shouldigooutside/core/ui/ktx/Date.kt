package now.shouldigooutside.core.ui.ktx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.blank
import now.shouldigooutside.core.resources.day_of_week_friday
import now.shouldigooutside.core.resources.day_of_week_monday
import now.shouldigooutside.core.resources.day_of_week_ordinal_first
import now.shouldigooutside.core.resources.day_of_week_ordinal_second
import now.shouldigooutside.core.resources.day_of_week_ordinal_th
import now.shouldigooutside.core.resources.day_of_week_ordinal_third
import now.shouldigooutside.core.resources.day_of_week_saturday
import now.shouldigooutside.core.resources.day_of_week_short_friday
import now.shouldigooutside.core.resources.day_of_week_short_monday
import now.shouldigooutside.core.resources.day_of_week_short_saturday
import now.shouldigooutside.core.resources.day_of_week_short_sunday
import now.shouldigooutside.core.resources.day_of_week_short_thursday
import now.shouldigooutside.core.resources.day_of_week_short_tuesday
import now.shouldigooutside.core.resources.day_of_week_short_wednesday
import now.shouldigooutside.core.resources.day_of_week_sunday
import now.shouldigooutside.core.resources.day_of_week_thursday
import now.shouldigooutside.core.resources.day_of_week_tuesday
import now.shouldigooutside.core.resources.day_of_week_wednesday
import now.shouldigooutside.core.resources.month_april
import now.shouldigooutside.core.resources.month_august
import now.shouldigooutside.core.resources.month_december
import now.shouldigooutside.core.resources.month_february
import now.shouldigooutside.core.resources.month_january
import now.shouldigooutside.core.resources.month_july
import now.shouldigooutside.core.resources.month_june
import now.shouldigooutside.core.resources.month_march
import now.shouldigooutside.core.resources.month_may
import now.shouldigooutside.core.resources.month_november
import now.shouldigooutside.core.resources.month_october
import now.shouldigooutside.core.resources.month_september
import now.shouldigooutside.core.resources.time_ago_a_minute
import now.shouldigooutside.core.resources.time_ago_at
import now.shouldigooutside.core.resources.time_ago_minutes
import now.shouldigooutside.core.resources.time_ago_moments
import now.shouldigooutside.core.resources.time_am
import now.shouldigooutside.core.resources.time_pm
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalAppExperience
import now.shouldigooutside.core.ui.asDisplay
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.preview.AppPreview
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

public val Month.text: StringResource
    get() = when (this) {
        Month.JANUARY -> Res.string.month_january
        Month.FEBRUARY -> Res.string.month_february
        Month.MARCH -> Res.string.month_march
        Month.APRIL -> Res.string.month_april
        Month.MAY -> Res.string.month_may
        Month.JUNE -> Res.string.month_june
        Month.JULY -> Res.string.month_july
        Month.AUGUST -> Res.string.month_august
        Month.SEPTEMBER -> Res.string.month_september
        Month.OCTOBER -> Res.string.month_october
        Month.NOVEMBER -> Res.string.month_november
        Month.DECEMBER -> Res.string.month_december
    }

public val Month.shortText: StringResource
    get() = when (this) {
        Month.JANUARY -> Res.string.month_january
        Month.FEBRUARY -> Res.string.month_february
        Month.MARCH -> Res.string.month_march
        Month.APRIL -> Res.string.month_april
        Month.MAY,
        Month.JUNE,
        Month.JULY,
        -> text
        Month.AUGUST -> Res.string.month_august
        Month.SEPTEMBER -> Res.string.month_september
        Month.OCTOBER -> Res.string.month_october
        Month.NOVEMBER -> Res.string.month_november
        Month.DECEMBER -> Res.string.month_december
    }

public val DayOfWeek.text: StringResource
    get() = when (this) {
        DayOfWeek.MONDAY -> Res.string.day_of_week_monday
        DayOfWeek.TUESDAY -> Res.string.day_of_week_tuesday
        DayOfWeek.WEDNESDAY -> Res.string.day_of_week_wednesday
        DayOfWeek.THURSDAY -> Res.string.day_of_week_thursday
        DayOfWeek.FRIDAY -> Res.string.day_of_week_friday
        DayOfWeek.SATURDAY -> Res.string.day_of_week_saturday
        DayOfWeek.SUNDAY -> Res.string.day_of_week_sunday
    }

public val DayOfWeek.textShort: StringResource
    get() = when (this) {
        DayOfWeek.MONDAY -> Res.string.day_of_week_short_monday
        DayOfWeek.TUESDAY -> Res.string.day_of_week_short_tuesday
        DayOfWeek.WEDNESDAY -> Res.string.day_of_week_short_wednesday
        DayOfWeek.THURSDAY -> Res.string.day_of_week_short_thursday
        DayOfWeek.FRIDAY -> Res.string.day_of_week_short_friday
        DayOfWeek.SATURDAY -> Res.string.day_of_week_short_saturday
        DayOfWeek.SUNDAY -> Res.string.day_of_week_short_sunday
    }

private val LocalDate.dayWithOrdinal: StringResource
    get() {
        val day = day
        return when (day % 10) {
            1 -> if (day != 11) Res.string.day_of_week_ordinal_first else Res.string.day_of_week_ordinal_th
            2 -> if (day != 12) Res.string.day_of_week_ordinal_second else Res.string.day_of_week_ordinal_th
            3 -> if (day != 13) Res.string.day_of_week_ordinal_third else Res.string.day_of_week_ordinal_th
            else -> Res.string.day_of_week_ordinal_th
        }
    }

@Composable
public fun LocalDate.monthText(short: Boolean = false): String {
    val res = remember(this, short) { if (short) month.shortText else month.text }
    return stringResource(res)
}

@Composable
public fun LocalDate.dayWithOrdinalText(): String {
    val res = remember(this) { dayWithOrdinal }
    return "${day}${stringResource(res)}"
}

@Composable
public fun LocalDate.rememberMonthDayString(
    separator: String = " ",
    shortMonth: Boolean = false,
    ordinal: Boolean = false,
    includeDayOfWeek: Boolean = false,
    dayOfWeekShort: Boolean = false,
): String {
    val dayOfWeekText = remember(includeDayOfWeek, dayOfWeekShort) {
        if (!includeDayOfWeek) {
            Res.string.blank
        } else {
            if (dayOfWeekShort) dayOfWeek.textShort else dayOfWeek.text
        }
    }.get()

    val monthText = monthText(shortMonth)
    val dayText = if (ordinal) dayWithOrdinalText() else day.toString()
    return remember(dayOfWeekText, monthText, dayText) {
        "$dayOfWeekText$separator$monthText$separator$dayText"
    }
}

@Composable
public fun DayOfWeek.text(): String {
    val res = remember(this) { text }
    return stringResource(res)
}

@Composable
public fun DayOfWeek.textShort(): String {
    val res = remember(this) { textShort }
    return stringResource(res)
}

@Composable
public fun LocalTime.text(use24Hours: Boolean = LocalAppExperience.current.use24HourFormat): String {
    if (use24Hours) {
        return remember(this) {
            "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        }
    } else {
        val (hour, minute, suffix) = remember(this) {
            val hourValue = if (hour > 12) hour - 12 else hour
            val minuteValue = minute.toString().padStart(2, '0')
            val suffix = if (hour > 11) Res.string.time_pm else Res.string.time_am
            Triple(hourValue, minuteValue, suffix)
        }

        val suffixString = suffix.get()
        return remember(hour, minute, suffixString) {
            "$hour:$minute $suffixString"
        }
    }
}

@Composable
public fun Instant.rememberTimeAgo(
    now: Instant = Clock.System.now(),
    use24Hours: Boolean = LocalAppExperience.current.use24HourFormat,
): String {
    val seconds = remember(this, now) {
        val duration = now - this
        abs(duration.inWholeSeconds)
    }

    return when {
        seconds < 30 -> {
            Res.string.time_ago_moments.get()
        }
        seconds < 120 -> {
            Res.string.time_ago_a_minute.get()
        }
        seconds < 3600 -> {
            val minutes = seconds / 60
            if (minutes == 1L) {
                Res.string.time_ago_a_minute.get()
            } else {
                Res.string.time_ago_minutes.get(minutes)
            }
        }
        else -> {
            val time = remember(this) {
                toLocalDateTime(TimeZone.currentSystemDefault()).time
            }

            Res.string.time_ago_at.get(time.text(use24Hours))
        }
    }
}

@Preview
@Composable
private fun TimeAgoPreview() {
    AppPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Column {
                Text("Now", style = AppTheme.typography.h4.asDisplay)
                Text(Clock.System.now().rememberTimeAgo())
            }

            Column {
                Text("25 seconds ago", style = AppTheme.typography.h4.asDisplay)
                Text(
                    Clock.System
                        .now()
                        .minus(25.seconds)
                        .rememberTimeAgo(),
                )
            }

            Column {
                Text("1 minute ago", style = AppTheme.typography.h4.asDisplay)
                Text(
                    Clock.System
                        .now()
                        .minus(1.minutes)
                        .rememberTimeAgo(),
                )
            }

            Column {
                Text("13 minutes ago", style = AppTheme.typography.h4.asDisplay)
                Text(
                    Clock.System
                        .now()
                        .minus(13.minutes)
                        .rememberTimeAgo(),
                )
            }

            Column {
                Text("1 hour ago", style = AppTheme.typography.h4.asDisplay)
                Text(
                    Clock.System
                        .now()
                        .minus(1.hours)
                        .rememberTimeAgo(),
                )
            }

            Column {
                Text("1 day ago", style = AppTheme.typography.h4.asDisplay)
                Text(
                    Clock.System
                        .now()
                        .minus(1.hours)
                        .rememberTimeAgo(),
                )
            }
        }
    }
}
