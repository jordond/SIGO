package app.sigot.core.platform.di

import app.sigot.core.platform.ClientIdProvider
import app.sigot.core.platform.DefaultLocationManager
import app.sigot.core.platform.LocationManager
import app.sigot.core.platform.createAutocomplete
import app.sigot.core.platform.createGeocoder
import app.sigot.core.platform.createGeolocator
import app.sigot.core.platform.internal.SettingsClientIdProvider
import app.sigot.core.platform.locationPermissionController
import app.sigot.core.platform.store.Store
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.connectivity.Connectivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public inline fun <reified T> getKoinInstance(): T =
    object : KoinComponent {
        @Suppress("UndeclaredKoinUsage")
        val value: T by inject()
    }.value

public fun platformModule(): Module =
    module {
        platformConfig()

        single<ClientIdProvider> {
            val store: Store<String> = Store.storeOf(
                filename = "client-id.json",
                type = Store.Type.Persistent,
            )
            SettingsClientIdProvider(store)
        }

        single<Connectivity> { getConnectivity() }

        single { createAutocomplete() }
        single<Geocoder> { createGeocoder() }
        single<Geolocator> { createGeolocator() }
        single<LocationPermissionController> { locationPermissionController() }
        factoryOf(::DefaultLocationManager) bind LocationManager::class
    }

internal expect fun Module.platformConfig()

internal expect fun getConnectivity(): Connectivity
