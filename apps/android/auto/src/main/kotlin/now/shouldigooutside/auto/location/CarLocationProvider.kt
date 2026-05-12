package now.shouldigooutside.auto.location

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
        settingsRepo.update { state ->
            state.copy(
                lastLocation = fix,
                lastLocationUpdate = nowProvider(),
            )
        }
        return true
    }
}
