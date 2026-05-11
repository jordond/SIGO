package now.shouldigooutside.auto

import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.test.FakeForecastStateHolder
import now.shouldigooutside.test.FakeSettingsRepo
import now.shouldigooutside.test.testForecast
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SigoSessionOrchestratorTest {
    @Test
    fun start_kicksFetch_andInvokesInvalidateOnStateEmission() =
        runTest {
            val holder = FakeForecastStateHolder()
            var invalidateCount = 0
            val orchestratorScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val orchestrator = SigoSessionOrchestrator(
                forecastStateHolder = holder,
                settingsRepo = FakeSettingsRepo(),
                getActivitiesScoreUseCase = FakeGetActivitiesScoreUseCase(),
                carLocationProvider = null,
                activityScoresSink = MutableStateFlow(persistentListOf()),
            )

            orchestrator.start(scope = orchestratorScope) { invalidateCount++ }
            advanceUntilIdle()

            holder.fetchCount shouldBe 1
            // StateFlow emits the initial value (Loading) on subscribe
            invalidateCount shouldBe 1

            orchestratorScope.cancel()
        }

    @Test
    fun start_invokesInvalidateOnSubsequentEmission() =
        runTest {
            val holder = FakeForecastStateHolder()
            var invalidateCount = 0
            val orchestratorScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val orchestrator = SigoSessionOrchestrator(
                forecastStateHolder = holder,
                settingsRepo = FakeSettingsRepo(),
                getActivitiesScoreUseCase = FakeGetActivitiesScoreUseCase(),
                carLocationProvider = null,
                activityScoresSink = MutableStateFlow(persistentListOf()),
            )

            orchestrator.start(scope = orchestratorScope) { invalidateCount++ }
            advanceUntilIdle()

            // Emit a distinct new value
            holder.emit(AsyncResult.Success(testForecast()))
            advanceUntilIdle()

            holder.fetchCount shouldBe 1
            invalidateCount shouldBe 2

            orchestratorScope.cancel()
        }

    @Test
    fun start_doesNotInvalidate_onDuplicateEmission() =
        runTest {
            val holder = FakeForecastStateHolder(initial = AsyncResult.Loading)
            var invalidateCount = 0
            val orchestratorScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val orchestrator = SigoSessionOrchestrator(
                forecastStateHolder = holder,
                settingsRepo = FakeSettingsRepo(),
                getActivitiesScoreUseCase = FakeGetActivitiesScoreUseCase(),
                carLocationProvider = null,
                activityScoresSink = MutableStateFlow(persistentListOf()),
            )

            orchestrator.start(scope = orchestratorScope) { invalidateCount++ }
            advanceUntilIdle()

            // Emit the same value again — StateFlow suppresses duplicates
            holder.emit(AsyncResult.Loading)
            advanceUntilIdle()

            invalidateCount shouldBe 1

            orchestratorScope.cancel()
        }

    @Test
    fun start_populatesActivityScoresSink() =
        runTest {
            val holder = FakeForecastStateHolder()
            val sink = MutableStateFlow(persistentListOf<ActivityForecastScore>())
            val orchestratorScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val orchestrator = SigoSessionOrchestrator(
                forecastStateHolder = holder,
                settingsRepo = FakeSettingsRepo(),
                getActivitiesScoreUseCase = FakeGetActivitiesScoreUseCase(),
                carLocationProvider = null,
                activityScoresSink = sink,
            )

            orchestrator.start(scope = orchestratorScope) {}
            advanceUntilIdle()

            // FakeGetActivitiesScoreUseCase emits an empty list
            sink.value shouldBe persistentListOf()

            orchestratorScope.cancel()
        }
}
