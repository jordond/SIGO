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

    @Test
    fun primeLocationRepo_writesSettings_onCarHardwareSuccess() =
        runTest {
            val settingsRepo = FakeSettingsRepo(
                initial = Settings(
                    firstLaunch = kotlin.time.Instant.fromEpochSeconds(0),
                    useCustomLocation = false,
                ),
            )
            val fakeFix = Location(latitude = 1.0, longitude = 2.0)
            val carHardware = StubCarHardware(fix = fakeFix, hasPermission = true)
            val provider = CarLocationProvider(
                settingsRepo = settingsRepo,
                carHardware = carHardware,
                nowProvider = { kotlin.time.Instant.fromEpochSeconds(123) },
            )

            val primed = provider.primeLocationRepo()

            primed shouldBe true
            settingsRepo.settings.value.lastLocation shouldBe fakeFix
            settingsRepo.settings.value.lastLocationUpdate shouldBe kotlin.time.Instant.fromEpochSeconds(123)
        }

    @Test
    fun primeLocationRepo_returnsFalse_whenPermissionDenied() =
        runTest {
            val settingsRepo = FakeSettingsRepo(
                initial = Settings(
                    firstLaunch = kotlin.time.Instant.fromEpochSeconds(0),
                    useCustomLocation = false,
                ),
            )
            val carHardware = StubCarHardware(fix = null, hasPermission = false)
            val provider = CarLocationProvider(
                settingsRepo = settingsRepo,
                carHardware = carHardware,
                nowProvider = { kotlin.time.Instant.fromEpochSeconds(0) },
            )

            provider.primeLocationRepo() shouldBe false
        }

    @Test
    fun primeLocationRepo_returnsFalse_onCarHardwareTimeout() =
        runTest {
            val settingsRepo = FakeSettingsRepo(
                initial = Settings(
                    firstLaunch = kotlin.time.Instant.fromEpochSeconds(0),
                    useCustomLocation = false,
                ),
            )
            val carHardware = StubCarHardware(fix = null, hasPermission = true)
            val initialLastLocation = settingsRepo.settings.value.lastLocation
            val provider = CarLocationProvider(
                settingsRepo = settingsRepo,
                carHardware = carHardware,
                nowProvider = { kotlin.time.Instant.fromEpochSeconds(0) },
            )

            provider.primeLocationRepo() shouldBe false
            settingsRepo.settings.value.lastLocation shouldBe initialLastLocation
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

internal class StubCarHardware(
    private val fix: Location?,
    private val hasPermission: Boolean,
) : CarHardwareLocationSource {
    override suspend fun requestSingleFix(timeoutMs: Long): Location? = fix

    override fun hasPermission(): Boolean = hasPermission
}
