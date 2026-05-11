package now.shouldigooutside.auto.screens

import androidx.car.app.model.ListTemplate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.auto.format.fakeAutoStrings
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.test.testForecast
import kotlin.test.Test

class AlertsTemplateBuilderTest {
    private val strings = fakeAutoStrings()
    private val formatter = CarForecastFormatter(strings)

    @Test
    fun build_returnsListTemplate_withAlertsFromForecast() {
        val forecast = testForecast(
            alerts = persistentListOf(
                Alert(title = "Storm", description = "desc"),
                Alert(title = "Wind", description = "desc"),
            ),
        )
        val builder = AlertsTemplateBuilder(formatter, strings)

        val template = builder.build(
            status = AsyncResult.Success(forecast),
            onAlertClick = {},
        )

        template.shouldBeInstanceOf<ListTemplate>()
        (template as ListTemplate).singleList?.items?.size shouldBe 2
    }
}
