package now.shouldigooutside.auto.di

import androidx.car.app.CarContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.location.AndroidCarHardwareLocationSource
import now.shouldigooutside.auto.location.CarLocationProvider
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.auto_alert_single
import now.shouldigooutside.core.resources.auto_alerts_count
import now.shouldigooutside.core.resources.auto_app_name
import now.shouldigooutside.core.resources.auto_feels_like_short
import now.shouldigooutside.core.resources.auto_forecast_unavailable
import now.shouldigooutside.core.resources.auto_hourly_forecast
import now.shouldigooutside.core.resources.auto_location_failed
import now.shouldigooutside.core.resources.auto_open_phone
import now.shouldigooutside.core.resources.auto_precip_short
import now.shouldigooutside.core.resources.auto_refresh
import now.shouldigooutside.core.resources.auto_retry
import now.shouldigooutside.core.resources.auto_stale_updated_hours
import now.shouldigooutside.core.resources.auto_stale_updated_minutes
import now.shouldigooutside.core.resources.auto_wind_short
import now.shouldigooutside.core.resources.score_maybe
import now.shouldigooutside.core.resources.score_no
import now.shouldigooutside.core.resources.score_yes
import now.shouldigooutside.core.resources.unit_temperature_celsius
import now.shouldigooutside.core.resources.unit_temperature_fahrenheit
import now.shouldigooutside.core.resources.unit_temperature_kelvin
import now.shouldigooutside.core.resources.unit_wind_knots
import now.shouldigooutside.core.resources.unit_wind_kph
import now.shouldigooutside.core.resources.unit_wind_mph
import now.shouldigooutside.core.resources.unit_wind_ms
import org.jetbrains.compose.resources.getString
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.time.Clock

private suspend fun resolveAutoStrings(): AutoStrings {
    val staleMinutesFmt = getString(Res.string.auto_stale_updated_minutes)
    val staleHoursFmt = getString(Res.string.auto_stale_updated_hours)
    val feelsLikeFmt = getString(Res.string.auto_feels_like_short)
    val windShortFmt = getString(Res.string.auto_wind_short)
    val precipShortFmt = getString(Res.string.auto_precip_short)
    val alertsCountFmt = getString(Res.string.auto_alerts_count)
    val alertSingle = getString(Res.string.auto_alert_single)
    return AutoStrings(
        appName = getString(Res.string.auto_app_name),
        refresh = getString(Res.string.auto_refresh),
        hourlyForecast = getString(Res.string.auto_hourly_forecast),
        openPhone = getString(Res.string.auto_open_phone),
        locationFailed = getString(Res.string.auto_location_failed),
        forecastUnavailable = getString(Res.string.auto_forecast_unavailable),
        retry = getString(Res.string.auto_retry),
        scoreYes = getString(Res.string.score_yes),
        scoreNo = getString(Res.string.score_no),
        scoreMaybe = getString(Res.string.score_maybe),
        staleMinutes = { mins -> staleMinutesFmt.format(mins) },
        staleHours = { hours -> staleHoursFmt.format(hours) },
        feelsLikeShort = { s -> feelsLikeFmt.format(s) },
        windShort = { s -> windShortFmt.format(s) },
        precipShort = { p -> precipShortFmt.format(p) },
        alertsCount = { c -> if (c == 1) alertSingle else alertsCountFmt.format(c) },
        tempCelsius = getString(Res.string.unit_temperature_celsius),
        tempFahrenheit = getString(Res.string.unit_temperature_fahrenheit),
        tempKelvin = getString(Res.string.unit_temperature_kelvin),
        windKph = getString(Res.string.unit_wind_kph),
        windMph = getString(Res.string.unit_wind_mph),
        windMs = getString(Res.string.unit_wind_ms),
        windKnots = getString(Res.string.unit_wind_knots),
    )
}

public fun autoModule(): Module =
    module {
        single<suspend () -> AutoStrings> {
            val mutex = Mutex()
            var cached: AutoStrings? = null
            val provider: suspend () -> AutoStrings = {
                cached ?: mutex.withLock {
                    cached ?: resolveAutoStrings().also { cached = it }
                }
            }
            provider
        }

        factory<(CarContext) -> CarLocationProvider?> {
            { carContext: CarContext ->
                CarLocationProvider(
                    settingsRepo = get(),
                    carHardware = AndroidCarHardwareLocationSource(carContext),
                    nowProvider = { Clock.System.now() },
                )
            }
        }
    }
