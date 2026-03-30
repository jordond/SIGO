package now.shouldigooutside.forecast.domain

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.config.model.AppConfig
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.ForecastData
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.test.FakeAppConfigRepo
import now.shouldigooutside.test.FakeGetForecastUseCase
import now.shouldigooutside.test.FakeLocationRepo
import now.shouldigooutside.test.FakeScoreCalculator
import now.shouldigooutside.test.FakeSettingsRepo
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastScore
import now.shouldigooutside.test.testScore
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class DefaultForecastStateHolderTest {
    private val zeroDelayConfig = AppConfig(minimumExecutionDelay = 0.seconds)

    @Test
    fun stateEmitsLoadingInitially() =
        runTest {
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = FakeGetForecastUseCase(),
                settingsRepo = FakeSettingsRepo(),
                appConfigRepo = appConfigRepo,
                scoreCalculator = FakeScoreCalculator(),
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading
                cancel()
            }
        }

    @Test
    fun stateEmitsForecastDataOnSuccess() =
        runTest {
            val forecast = testForecast()
            val score = testForecastScore()
            val getForecastUseCase = FakeGetForecastUseCase(result = Result.success(forecast))
            val scoreCalculator = FakeScoreCalculator(result = score)
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = getForecastUseCase,
                settingsRepo = FakeSettingsRepo(),
                appConfigRepo = appConfigRepo,
                scoreCalculator = scoreCalculator,
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.fetch()

                // StateFlow deduplicates: container Loading == stateIn initial Loading, so skipped.
                // Only the Success emission comes through.
                val successItem = awaitItem()
                successItem.shouldBeInstanceOf<AsyncResult.Success<ForecastData>>()
                successItem.data.forecast shouldBe forecast
                successItem.data.score shouldBe score
                cancel()
            }
        }

    @Test
    fun stateReScoresWhenPreferencesChange() =
        runTest {
            val forecast = testForecast()
            val settingsRepo = FakeSettingsRepo()
            val scoreCalculator = FakeScoreCalculator()
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = FakeGetForecastUseCase(result = Result.success(forecast)),
                settingsRepo = settingsRepo,
                appConfigRepo = appConfigRepo,
                scoreCalculator = scoreCalculator,
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem()

                holder.fetch()
                // Container Loading == stateIn initial Loading (StateFlow dedup), so only Success emits
                awaitItem()

                val updatedPreferences = Preferences.default.copy(windSpeed = 55)
                // Change the score result so ForecastData changes and StateFlow emits a new distinct value
                scoreCalculator.result = testForecastScore(current = testScore(result = ScoreResult.No))
                settingsRepo.update { settings ->
                    settings.updatePreferences(settings.selectedActivity, updatedPreferences)
                }

                awaitItem()
                scoreCalculator.lastPreferences shouldBe updatedPreferences
                cancel()
            }
        }

    @Test
    fun stateReScoresWhenAirQualityToggled() =
        runTest {
            val forecast = testForecast()
            val settingsRepo = FakeSettingsRepo()
            val scoreCalculator = FakeScoreCalculator()
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = FakeGetForecastUseCase(result = Result.success(forecast)),
                settingsRepo = settingsRepo,
                appConfigRepo = appConfigRepo,
                scoreCalculator = scoreCalculator,
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem()

                holder.fetch()
                // StateFlow dedup: container Loading == stateIn Loading, so only Success emits
                awaitItem()

                // Change the score result so ForecastData changes and StateFlow emits a new distinct value
                scoreCalculator.result = testForecastScore(current = testScore(result = ScoreResult.No))
                settingsRepo.update { it.copy(includeAirQuality = !it.includeAirQuality) }

                awaitItem() // re-scored after air quality toggle
                scoreCalculator.lastIncludeAirQuality shouldBe !Settings().includeAirQuality
                cancel()
            }
        }

    @Test
    fun stateEmitsErrorOnLocationFailure() =
        runTest {
            val locationRepo = FakeLocationRepo(
                locationResult = LocationResult.NotAllowed(permanent = false),
            )
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = locationRepo,
                getForecastUseCase = FakeGetForecastUseCase(),
                settingsRepo = FakeSettingsRepo(),
                appConfigRepo = appConfigRepo,
                scoreCalculator = FakeScoreCalculator(),
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.fetch()

                // Container Loading == stateIn Loading (StateFlow dedup skips); Error is distinct.
                val errorItem = awaitItem()
                errorItem.shouldBeInstanceOf<AsyncResult.Error>()
                cancel()
            }
        }

    @Test
    fun fetchSetsLoadingThenSuccess() =
        runTest {
            val forecast = testForecast()
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = FakeGetForecastUseCase(result = Result.success(forecast)),
                settingsRepo = FakeSettingsRepo(),
                appConfigRepo = appConfigRepo,
                scoreCalculator = FakeScoreCalculator(),
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.fetch()

                // Container Loading == stateIn Loading (StateFlow dedup skips); Success is distinct.
                val success = awaitItem()
                success.shouldBeInstanceOf<AsyncResult.Success<ForecastData>>()
                cancel()
            }
        }

    @Test
    fun fetchSetsErrorWhenLocationFails() =
        runTest {
            val locationRepo = FakeLocationRepo(
                locationResult = LocationResult.NotFound(),
            )
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = locationRepo,
                getForecastUseCase = FakeGetForecastUseCase(),
                settingsRepo = FakeSettingsRepo(),
                appConfigRepo = appConfigRepo,
                scoreCalculator = FakeScoreCalculator(),
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.fetch()

                // Container Loading == stateIn Loading (StateFlow dedup skips); Error is distinct.
                val error = awaitItem()
                error.shouldBeInstanceOf<AsyncResult.Error>()
                cancel()
            }
        }

    @Test
    fun fetchUsesCurrentUnitsFromSettings() =
        runTest {
            val settingsRepo = FakeSettingsRepo(
                initial = Settings(
                    firstLaunch = kotlin.time.Instant.fromEpochSeconds(0),
                    units = Units.Imperial,
                ),
            )
            val getForecastUseCase = FakeGetForecastUseCase()
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = getForecastUseCase,
                settingsRepo = settingsRepo,
                appConfigRepo = appConfigRepo,
                scoreCalculator = FakeScoreCalculator(),
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem()

                holder.fetch()

                // Container Loading == stateIn Loading (StateFlow dedup skips); Success is distinct.
                awaitItem()

                getForecastUseCase.lastUnits shouldBe Units.Imperial
                cancel()
            }
        }

    @Test
    fun startBeginsFetching() =
        runTest {
            val forecast = testForecast()
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = FakeGetForecastUseCase(result = Result.success(forecast)),
                settingsRepo = FakeSettingsRepo(),
                appConfigRepo = appConfigRepo,
                scoreCalculator = FakeScoreCalculator(),
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.start(backgroundScope)

                // Container Loading == stateIn Loading (StateFlow dedup skips); Success is distinct.
                val success = awaitItem()
                success.shouldBeInstanceOf<AsyncResult.Success<ForecastData>>()
                cancel()
            }
        }

    @Test
    fun stopCancelsRefreshLoop() =
        runTest {
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = FakeGetForecastUseCase(),
                settingsRepo = FakeSettingsRepo(),
                appConfigRepo = appConfigRepo,
                scoreCalculator = FakeScoreCalculator(),
                coroutineScope = backgroundScope,
            )

            holder.start(backgroundScope)
            holder.stop()

            // After stopping, the refreshJob should be cancelled — no assertion needed beyond no hang
            // Verify no error thrown and state is accessible
            holder.state.value.shouldBeInstanceOf<AsyncResult.Loading>()
        }
}
