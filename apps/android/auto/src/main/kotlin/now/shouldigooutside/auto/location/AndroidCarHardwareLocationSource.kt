package now.shouldigooutside.auto.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.car.app.CarContext
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.common.OnCarDataAvailableListener
import androidx.car.app.hardware.info.CarHardwareLocation
import androidx.car.app.hardware.info.CarSensors
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import now.shouldigooutside.core.model.location.Location

internal class AndroidCarHardwareLocationSource(
    private val carContext: CarContext,
) : CarHardwareLocationSource {
    private val logger = Logger.withTag("AndroidCarHardware")

    override fun hasPermission(): Boolean =
        carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            carContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    override suspend fun requestSingleFix(timeoutMs: Long): Location? =
        withTimeoutOrNull(timeoutMs) {
            val manager = carContext.getCarService(CarHardwareManager::class.java)
            val deferred = CompletableDeferred<Location?>()

            val listener = OnCarDataAvailableListener<CarHardwareLocation> { hardwareLocation ->
                val androidLoc = hardwareLocation.location.value
                if (androidLoc != null) {
                    deferred.complete(
                        Location(
                            latitude = androidLoc.latitude,
                            longitude = androidLoc.longitude,
                        ),
                    )
                } else {
                    deferred.complete(null)
                }
            }

            runCatching {
                manager.carSensors.addCarHardwareLocationListener(
                    CarSensors.UPDATE_RATE_NORMAL,
                    carContext.mainExecutor,
                    listener,
                )
            }.onFailure { error ->
                logger.w(error) { "Car hardware location unavailable" }
                deferred.complete(null)
            }

            try {
                deferred.await()
            } finally {
                runCatching { manager.carSensors.removeCarHardwareLocationListener(listener) }
            }
        }
}
