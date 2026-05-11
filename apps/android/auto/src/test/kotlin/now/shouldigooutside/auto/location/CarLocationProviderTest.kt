package now.shouldigooutside.auto.location

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.test.FakeSettingsRepo
import kotlin.test.Test

class CarLocationProviderTest {
    @Test
    fun primeLocationRepo_skips_whenUseCustomLocationIsTrue() =
        runTest {
            val settingsRepo = FakeSettingsRepo(
                initial = Settings(
                    firstLaunch = kotlin.time.Instant.fromEpochSeconds(0),
                    useCustomLocation = true,
                ),
            )
            val carHardware = NoopCarHardware()
            val provider = CarLocationProvider(
                settingsRepo = settingsRepo,
                carHardware = carHardware,
                nowProvider = { kotlin.time.Instant.fromEpochSeconds(0) },
            )

            val primed = provider.primeLocationRepo()

            primed shouldBe false
            carHardware.attemptCount shouldBe 0
        }
}

internal class NoopCarHardware : CarHardwareLocationSource {
    var attemptCount = 0

    override suspend fun requestSingleFix(timeoutMs: Long): Location? {
        attemptCount++
        return null
    }

    override fun hasPermission(): Boolean = false
}
