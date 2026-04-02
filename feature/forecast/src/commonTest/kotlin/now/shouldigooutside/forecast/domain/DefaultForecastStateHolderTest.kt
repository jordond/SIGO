package now.shouldigooutside.forecast.domain

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.config.model.AppConfig
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.test.FakeAppConfigRepo
import now.shouldigooutside.test.FakeGetForecastUseCase
import now.shouldigooutside.test.FakeLocationRepo
import now.shouldigooutside.test.FakeSettingsRepo
import now.shouldigooutside.test.testForecast
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
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading
                cancel()
            }
        }

    @Test
    fun stateEmitsForecastOnSuccess() =
        runTest {
            val forecast = testForecast()
            val getForecastUseCase = FakeGetForecastUseCase(result = Result.success(forecast))
            val appConfigRepo = FakeAppConfigRepo(zeroDelayConfig)
            val holder = DefaultForecastStateHolder(
                locationRepo = FakeLocationRepo(),
                getForecastUseCase = getForecastUseCase,
                settingsRepo = FakeSettingsRepo(),
                appConfigRepo = appConfigRepo,
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.fetch()

                val successItem = awaitItem()
                successItem.shouldBeInstanceOf<AsyncResult.Success<Forecast>>()
                successItem.data shouldBe forecast
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
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.fetch()

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
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.fetch()

                val success = awaitItem()
                success.shouldBeInstanceOf<AsyncResult.Success<Forecast>>()
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
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.fetch()

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
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem()

                holder.fetch()

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
                coroutineScope = backgroundScope,
            )

            holder.state.test {
                awaitItem() shouldBe AsyncResult.Loading

                holder.start(backgroundScope)

                val success = awaitItem()
                success.shouldBeInstanceOf<AsyncResult.Success<Forecast>>()
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
                coroutineScope = backgroundScope,
            )

            holder.start(backgroundScope)
            holder.stop()

            holder.state.value.shouldBeInstanceOf<AsyncResult.Loading>()
        }
}
