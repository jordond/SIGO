package now.shouldigooutside.auto.di

import androidx.car.app.CarContext
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.location.CarLocationProvider

internal fun interface AutoStringsProvider {
    suspend operator fun invoke(): AutoStrings
}

internal fun interface CarLocationProviderFactory {
    operator fun invoke(carContext: CarContext): CarLocationProvider?
}
