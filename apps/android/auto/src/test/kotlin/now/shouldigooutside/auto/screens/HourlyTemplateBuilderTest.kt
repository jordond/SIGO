package now.shouldigooutside.auto.screens

import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.auto.format.fakeAutoStrings
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastBlock
import now.shouldigooutside.test.testForecastDay
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class HourlyTemplateBuilderTest {
    private val strings = fakeAutoStrings()
    private val formatter = CarForecastFormatter(strings)
    private val now = Instant.fromEpochSeconds(1_715_000_000)

    @Test
    fun build_returnsListTemplate_onSuccess_withHourlyRows() {
        val hours = (0..7).map { offset ->
            testForecastBlock(instant = now + offset.hours)
        }
        val forecast = testForecast(today = testForecastDay(hours = hours))
        val builder = HourlyTemplateBuilder(formatter, strings, nowProvider = { now })

        val template = builder.build(
            status = AsyncResult.Success(forecast),
            settings = Settings(firstLaunch = Instant.fromEpochSeconds(0)),
        )

        template.shouldBeInstanceOf<ListTemplate>()
        (template as ListTemplate).singleList?.items?.size shouldBe 8
    }

    @Test
    fun build_returnsMessageTemplate_onLoadingOrError() {
        val builder = HourlyTemplateBuilder(formatter, strings, nowProvider = { now })

        val template = builder.build(
            status = AsyncResult.Loading,
            settings = Settings(firstLaunch = Instant.fromEpochSeconds(0)),
        )

        template.shouldBeInstanceOf<MessageTemplate>()
    }
}
