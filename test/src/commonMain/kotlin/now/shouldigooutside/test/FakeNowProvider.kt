package now.shouldigooutside.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import now.shouldigooutside.core.foundation.NowProvider
import kotlin.time.Duration
import kotlin.time.Instant

public class FakeNowProvider(
    public var instant: Instant = Instant.fromEpochSeconds(0),
) : NowProvider {
    override fun now(): Instant = instant

    override fun today(): LocalDate = instant.toLocalDate()

    override fun todayFlow(): Flow<LocalDate> = flow { emit(today()) }

    override fun durationFromNow(instant: Instant): Duration = now().minus(instant)
}

private fun Instant.toLocalDate(): LocalDate = this.toLocalDateTime(TimeZone.UTC).date
