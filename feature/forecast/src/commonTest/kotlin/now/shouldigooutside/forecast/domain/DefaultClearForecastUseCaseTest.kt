package now.shouldigooutside.forecast.domain

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.forecast.data.source.FakeForecastCache
import kotlin.test.Test

class DefaultClearForecastUseCaseTest {
    @Test
    fun clear_delegatesToCache() =
        runTest {
            val cache = FakeForecastCache()
            val useCase = DefaultClearForecastUseCase(cache)

            useCase.clear()

            cache.clearCalled shouldBe true
        }
}
