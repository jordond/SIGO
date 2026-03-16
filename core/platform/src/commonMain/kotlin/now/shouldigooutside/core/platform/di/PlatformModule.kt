package now.shouldigooutside.core.platform.di

import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.connectivity.Connectivity
import now.shouldigooutside.core.platform.ClientIdProvider
import now.shouldigooutside.core.platform.DefaultLocationManager
import now.shouldigooutside.core.platform.LocationManager
import now.shouldigooutside.core.platform.createAutocomplete
import now.shouldigooutside.core.platform.createGeocoder
import now.shouldigooutside.core.platform.createGeolocator
import now.shouldigooutside.core.platform.internal.SettingsClientIdProvider
import now.shouldigooutside.core.platform.locationPermissionController
import now.shouldigooutside.core.platform.store.Store
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
