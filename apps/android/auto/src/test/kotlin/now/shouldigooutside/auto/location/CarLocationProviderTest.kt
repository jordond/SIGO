package now.shouldigooutside.auto.location

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.settings.Settings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class CarLocationProviderTest {
    @Test
    fun primeLocationRepoWaitsForAsyncSettingsWrite() =
        runTest {
            val carFix = Location(latitude = 43.6532, longitude = -79.3832)
            val settingsRepo = AsyncSettingsRepo(scope = this, updateDelayMs = 50L)
            val provider = CarLocationProvider(
                settingsRepo = settingsRepo,
                carHardware = FakeCarHardwareLocationSource(fix = carFix),
                nowProvider = { updateTime },
            )

            assertTrue(provider.primeLocationRepo())

            assertEquals(carFix, settingsRepo.settings.value.lastLocation)
            assertEquals(updateTime, settingsRepo.settings.value.lastLocationUpdate)
        }

    @Test
    fun primeLocationRepoSkipsCarHardwareWhenPermissionIsMissing() =
        runTest {
            val hardware = FakeCarHardwareLocationSource(
                fix = Location(latitude = 43.6532, longitude = -79.3832),
                hasPermission = false,
            )
            val provider = CarLocationProvider(
                settingsRepo = AsyncSettingsRepo(scope = this, updateDelayMs = 0L),
                carHardware = hardware,
                nowProvider = { updateTime },
            )

            assertFalse(provider.primeLocationRepo())
            assertEquals(0, hardware.requestCount)
        }

    private class AsyncSettingsRepo(
        private val scope: CoroutineScope,
        private val updateDelayMs: Long,
    ) : SettingsRepo {
        private val initialSettings = Settings(firstLaunch = Instant.fromEpochSeconds(0))
        private val settingsFlow = MutableStateFlow(initialSettings)

        override val settings: StateFlow<Settings> = settingsFlow.asStateFlow()

        override fun update(block: (Settings) -> Settings) {
            scope.launch {
                delay(updateDelayMs)
                settingsFlow.value = block(settingsFlow.value)
            }
        }

        override fun reset() {
            update { initialSettings }
        }
    }

    private class FakeCarHardwareLocationSource(
        private val fix: Location?,
        private val hasPermission: Boolean = true,
    ) : CarHardwareLocationSource {
        var requestCount = 0
            private set

        override suspend fun requestSingleFix(timeoutMs: Long): Location? {
            requestCount++
            return fix
        }

        override fun hasPermission(): Boolean = hasPermission
    }

    private companion object {
        val updateTime: Instant = Instant.fromEpochSeconds(1)
    }
}
