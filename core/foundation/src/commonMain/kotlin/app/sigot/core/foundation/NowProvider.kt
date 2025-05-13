package app.sigot.core.foundation

import co.touchlab.kermit.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.coroutines.coroutineContext

public interface NowProvider {
    public fun now(): Instant

    public fun today(): LocalDate

    public fun todayFlow(): Flow<LocalDate>
}

internal class DefaultNowProvider(
    private val timezoneProvider: TimezoneProvider,
) : NowProvider {
    private val logger = Logger.withTag("NowProvider")

    override fun now(): Instant = Clock.System.now()

    override fun today(): LocalDate = Clock.System.todayIn(timezoneProvider.provide())

    override fun todayFlow(): Flow<LocalDate> =
        flow {
            var lastValue = today()
            emit(lastValue)
            while (coroutineContext.isActive) {
                // Calculate the duration until the next day then delay
                val tomorrow = today().plus(1, DateTimeUnit.DAY).atStartOfDayIn(timezoneProvider.provide())
                val duration = tomorrow - now()
                logger.d { "Duration until next day: $duration" }
                delay(duration)

                val value = today()
                logger.d { "Now: $value, Last: $lastValue" }
                if (value != lastValue) {
                    logger.i { "Emitting new date value: $value" }
                    emit(value)
                    lastValue = value
                }
            }
        }.distinctUntilChanged()
}
