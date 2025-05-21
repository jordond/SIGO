package app.sigot.core.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.sigot.core.resources.Res
import app.sigot.core.resources.blank
import app.sigot.core.resources.day_of_week_friday
import app.sigot.core.resources.day_of_week_monday
import app.sigot.core.resources.day_of_week_ordinal_first
import app.sigot.core.resources.day_of_week_ordinal_second
import app.sigot.core.resources.day_of_week_ordinal_th
import app.sigot.core.resources.day_of_week_ordinal_third
import app.sigot.core.resources.day_of_week_saturday
import app.sigot.core.resources.day_of_week_short_friday
import app.sigot.core.resources.day_of_week_short_monday
import app.sigot.core.resources.day_of_week_short_saturday
import app.sigot.core.resources.day_of_week_short_sunday
import app.sigot.core.resources.day_of_week_short_thursday
import app.sigot.core.resources.day_of_week_short_tuesday
import app.sigot.core.resources.day_of_week_short_wednesday
import app.sigot.core.resources.day_of_week_sunday
import app.sigot.core.resources.day_of_week_thursday
import app.sigot.core.resources.day_of_week_tuesday
import app.sigot.core.resources.day_of_week_wednesday
import app.sigot.core.resources.month_april
import app.sigot.core.resources.month_august
import app.sigot.core.resources.month_december
import app.sigot.core.resources.month_february
import app.sigot.core.resources.month_january
import app.sigot.core.resources.month_july
import app.sigot.core.resources.month_june
import app.sigot.core.resources.month_march
import app.sigot.core.resources.month_may
import app.sigot.core.resources.month_november
import app.sigot.core.resources.month_october
import app.sigot.core.resources.month_september
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

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
        val day = dayOfMonth
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
    return "${dayOfMonth}${stringResource(res)}"
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
    val dayText = if (ordinal) dayWithOrdinalText() else dayOfMonth.toString()
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
