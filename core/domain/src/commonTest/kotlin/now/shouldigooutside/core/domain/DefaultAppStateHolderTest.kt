package now.shouldigooutside.core.domain

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import kotlin.test.Test

class DefaultAppStateHolderTest {
    private val holder = DefaultAppStateHolder()

    @Test
    fun initialStateIsNow() {
        holder.state.value.period shouldBe ForecastPeriod.Now
    }

    @Test
    fun updateChangesPeriod() {
        holder.update(ForecastPeriod.Today)

        holder.state.value.period shouldBe ForecastPeriod.Today
    }

    @Test
    fun updateToSamePeriodIsIdempotent() {
        holder.update(ForecastPeriod.Tomorrow)
        holder.update(ForecastPeriod.Tomorrow)

        holder.state.value.period shouldBe ForecastPeriod.Tomorrow
    }
}
