package now.shouldigooutside.auto.screens

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.auto.format.fakeAutoStrings
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastScore
import kotlin.test.Test
import kotlin.time.Instant

class HomeTemplateBuilderTest {
    private val strings = fakeAutoStrings()
    private val formatter = CarForecastFormatter(strings)
    private val now = Instant.fromEpochSeconds(1_715_000_000)

    @Test
    fun buildResult_returnsNull_whenStatusLoadingAndNoCache() {
        val builder = HomeTemplateBuilder(formatter, strings, nowProvider = { now })

        val result = builder.buildResult(
            status = AsyncResult.Loading,
            settings = Settings(firstLaunch = Instant.fromEpochSeconds(0)),
            scores = persistentListOf(),
            onRefresh = {},
            onHourly = {},
            onAlerts = {},
        )

        result shouldBe null
    }

    @Test
    fun buildResult_returnsHomePaneResult_withRows_onSuccess() {
        val builder = HomeTemplateBuilder(formatter, strings, nowProvider = { now })
        val forecast = testForecast()
        val score = ActivityForecastScore(
            activity = Activity.General,
            preferences = Preferences.default,
            score = testForecastScore(),
        )

        val result = builder.buildResult(
            status = AsyncResult.Success(forecast),
            settings = Settings(firstLaunch = Instant.fromEpochSeconds(0)),
            scores = persistentListOf(score),
            onRefresh = {},
            onHourly = {},
            onAlerts = {},
        )

        result shouldNotBe null
        result!!.pane.rows.isNotEmpty() shouldBe true
    }
}
