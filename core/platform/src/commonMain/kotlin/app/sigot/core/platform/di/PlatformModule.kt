package app.sigot.core.platform.di

import app.sigot.core.platform.DefaultLocationManager
import app.sigot.core.platform.LocationManager
import app.sigot.core.platform.createGeocoder
import app.sigot.core.platform.createGeolocator
import app.sigot.core.platform.isDebug
import app.sigot.core.platform.locationPermissionController
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.connectivity.Connectivity
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val taggedLogger = co.touchlab.kermit.Logger
    .withTag("Ktor")

public inline fun <reified T> getKoinInstance(): T =
    object : KoinComponent {
        @Suppress("UndeclaredKoinUsage")
        val value: T by inject()
    }.value

public val defaultJson: Json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

public fun platformModule(): Module =
    module {
        platformConfig()

        single<Json> { defaultJson }

        single {
            HttpClient {
                expectSuccess = true

                install(ContentNegotiation) {
                    json(get<Json>())
                }

                install(Logging) {
                    level = if (isDebug) LogLevel.INFO else LogLevel.HEADERS
                    logger = object : Logger {
                        override fun log(message: String) {
                            taggedLogger.d { message }
                        }
                    }
                }
            }
        }

        single<Connectivity> { getConnectivity() }

        single<Geocoder> { createGeocoder() }
        single<Geolocator> { createGeolocator() }
        single<LocationPermissionController> { locationPermissionController() }
        factoryOf(::DefaultLocationManager) bind LocationManager::class
    }

internal expect fun Module.platformConfig()

internal expect fun getConnectivity(): Connectivity
