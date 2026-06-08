package now.shouldigooutside.auto.location

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.location.Location
import kotlin.time.Instant

internal interface CarHardwareLocationSource {
    suspend fun requestSingleFix(timeoutMs: Long): Location?

    fun hasPermission(): Boolean
}

internal class CarLocationProvider(
    private val settingsRepo: SettingsRepo,
    private val carHardware: CarHardwareLocationSource,
    private val nowProvider: () -> Instant,
) {
    suspend fun primeLocationRepo(): Boolean {
        if (settingsRepo.settings.value.useCustomLocation) return false
        if (!carHardware.hasPermission()) return false
        val fix = carHardware.requestSingleFix(timeoutMs = 2_000L) ?: return false
        val updateTime = nowProvider()
        settingsRepo.update { state ->
            state.copy(
                lastLocation = fix,
                lastLocationUpdate = updateTime,
            )
        }
        return withTimeoutOrNull(SETTINGS_WRITE_TIMEOUT_MS) {
            settingsRepo.settings.first { state ->
                state.lastLocation == fix && state.lastLocationUpdate == updateTime
            }
        } != null
    }

    private companion object {
        const val SETTINGS_WRITE_TIMEOUT_MS = 2_000L
    }
}
