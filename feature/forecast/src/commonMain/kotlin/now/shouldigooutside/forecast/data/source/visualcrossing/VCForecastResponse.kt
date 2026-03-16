package now.shouldigooutside.forecast.data.source.visualcrossing

import co.touchlab.kermit.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.foundation.NowProvider
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastDay
import now.shouldigooutside.core.model.forecast.Precipitation
import now.shouldigooutside.core.model.forecast.PrecipitationType
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.forecast.Temperature
import now.shouldigooutside.core.model.forecast.Wind
import now.shouldigooutside.core.model.location.Location
import kotlin.math.roundToInt
import kotlin.time.Instant

@Serializable
internal data class VCForecastResponse(
    @SerialName("queryCost")
    val queryCost: Int,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("resolvedAddress")
    val resolvedAddress: String,
    @SerialName("address")
    val address: String,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("days")
    val days: List<VCForecastBlock>,
    @SerialName("alerts")
    val alerts: List<VCAlert> = emptyList(),
    @SerialName("currentConditions")
    val currentConditions: VCForecastBlock,
)

/**
 * @property cloudCover how much of the sky is covered in cloud ranging from 0-100%
 * @property datetime – ISO 8601 formatted date, time or datetime value indicating the date and time of the
 * @property datetimeEpoch – number of seconds since 1st January 1970 in UTC time
 * @property dew the dew point in degrees
 * @property feelsLike what the temperature feels like accounting for heat index or wind chill.
 * Daily values are average values (mean) for the day.
 * @property feelsLikeMax (day only) – maximum feels like temperature at the location
 * @property feelsLikeMin (day only) – minimum feels like temperature at the location.
 * @property humidity relative humidity in %
 * @property icon – a fixed, machine readable summary that can be used to display an icon
 * @property moonPhase –  represents the fractional portion through the current moon lunation cycle ranging
 * from 0 (the new moon) to 0.5 (the full moon) and back to 1 (the next new moon).
 * @property precip the amount of liquid precipitation that fell or is predicted to fall in the period.
 * This includes the liquid-equivalent amount of any frozen precipitation such as snow or ice.
 * @property precipCover (days only) – the proportion of hours where there was non-zero precipitation
 * @property precipProb (forecast only) – the likelihood of measurable precipitation ranging from 0% to 100%
 * @property precipType (optional) an array indicating the type(s) of precipitation expected or that occurred.
 * Possible values include rain, snow, freezingrain and ice.
 * @property pressure the atmospheric pressure in millibars.
 * @property snow the amount of snow that fell or is predicted to fall in the period.
 * @property snowDepth the depth of snow on the ground.
 * @property sunrise (day only) – The formatted time of the sunrise (For example “2022-05-23T05:50:40”).
 * @property sunriseEpoch – sunrise time specified as number of seconds since 1st January 1970 in UTC time
 * @property sunset – The formatted time of the sunset (For example “2022-05-23T20:22:29”).
 * @property sunsetEpoch – sunset time specified as number of seconds since 1st January 1970 in UTC time
 * @property moonrise (day only, optional) – The formatted time of the moonrise (For example “2022-05-23T02:38:10”).
 * @property moonriseEpoch (day only, optional) – moonrise time specified as number of seconds since 1st January 1970 in UTC time
 * @property moonset (day only, optional) – The formatted time of the moonset (For example “2022-05-23T13:40:07”)
 * @property moonsetEpoch (day only, optional) – moonset time specified as number of seconds since 1st January 1970 in UTC time
 * @property temp – temperature at the location. Daily values are average values (mean) for the day.
 * @property tempMax (day only) – maximum temperature at the location.
 * @property tempMin (day only) – minimum temperature at the location.
 * @property uvIndex – a value between 0 and 10 indicating the level of ultra violet (UV) exposure for that
 * hour or day. 10 represents high level of exposure, and 0 represents no exposure. The UV index is
 * calculated based on amount of short wave solar radiation which in turn is a level the cloudiness, type
 * of cloud, time of day, time of year and location altitude. Daily values represent the maximum value of
 * the hourly values.
 * @property visibility – distance at which distant objects are visible
 * @property windDir the average wind direction in
 * @property windGust  instantaneous wind speed at a location – May be empty if it is not significantly
 * higher than the wind speed. Daily values are the maximum hourly value for the day.
 * @property windSpeed the sustained wind speed measured as the average wind speed that occurs during the
 * preceding one to two minutes. Daily values are the maximum hourly value for the day.
 * @property windSpeedMax (day only, optional) – maximum wind speed over the day.
 * @property windSpeedMean (day only , optional) – average (mean) wind speed over the day.
 * @property windSpeedMin (day only , optional) – minimum wind speed over the day.
 * @property solarRadiation – (W/m2) the solar radiation power at the instantaneous moment of the observation
 * (or forecast prediction). See the full solar radiation data documentation and Wind and Solar Energy pages.
 * @property solarEnergy – (MJ /m2) indicates the total energy from the sun that builds up over an hour or day
 * @property cloudCover how much of the sky is covered in cloud ranging from 0-100%
 * @property severeRisk value between 0 and 100 representing the risk of convective storms
 * (e.g. thunderstorms, hail and tornadoes). Severe risk is a scaled measure that combines a variety of
 * other fields such as the convective available potential energy (CAPE) and convective inhibition (CIN),
 * predicted rain and wind. Typically, a severe risk value less than 30 indicates a low risk,
 * between 30 and 70 a moderate risk and above 70 a high risk.
 */
@Serializable
internal data class VCForecastBlock(
    @SerialName("cloudcover")
    val cloudCover: Double,
    @SerialName("datetime")
    val datetime: String,
    @SerialName("datetimeEpoch")
    val datetimeEpoch: Long,
    @SerialName("dew")
    val dew: Double,
    @SerialName("feelslike")
    val feelsLike: Double,
    @SerialName("feelslikemax")
    val feelsLikeMax: Double? = null,
    @SerialName("feelslikemin")
    val feelsLikeMin: Double? = null,
    @SerialName("humidity")
    val humidity: Double,
    @SerialName("icon")
    val icon: String,
    @SerialName("moonphase")
    val moonPhase: Double = 0.0,
    @SerialName("precip")
    val precip: Double? = 0.0,
    @SerialName("precipcover")
    val precipCover: Double? = 0.0,
    @SerialName("precipprob")
    val precipProb: Double? = 0.0,
    @SerialName("preciptype")
    val precipType: List<String>? = null,
    @SerialName("pressure")
    val pressure: Double,
    @SerialName("snow")
    val snow: Double? = 0.0,
    @SerialName("snowdepth")
    val snowDepth: Double? = 0.0,
    @SerialName("sunrise")
    val sunrise: String? = null,
    @SerialName("sunriseEpoch")
    val sunriseEpoch: Long? = null,
    @SerialName("sunset")
    val sunset: String? = null,
    @SerialName("sunsetEpoch")
    val sunsetEpoch: Long? = null,
    @SerialName("moonrise")
    val moonrise: String? = null,
    @SerialName("moonriseEpoch")
    val moonriseEpoch: Long? = null,
    @SerialName("moonset")
    val moonset: String? = null,
    @SerialName("moonsetEpoch")
    val moonsetEpoch: Long? = null,
    @SerialName("temp")
    val temp: Double,
    @SerialName("tempmax")
    val tempMax: Double? = null,
    @SerialName("tempmin")
    val tempMin: Double? = null,
    @SerialName("uvindex")
    val uvIndex: Double,
    @SerialName("visibility")
    val visibility: Double,
    @SerialName("winddir")
    val windDir: Double,
    @SerialName("windgust")
    val windGust: Double,
    @SerialName("windspeed")
    val windSpeed: Double,
    @SerialName("windspeedmax")
    val windSpeedMax: Double? = null,
    @SerialName("windSpeedmean")
    val windSpeedMean: Double? = null,
    @SerialName("windSpeedmin")
    val windSpeedMin: Double? = null,
    @SerialName("solarradiation")
    val solarRadiation: Double,
    @SerialName("solarenergy")
    val solarEnergy: Double,
    @SerialName("severerisk")
    val severeRisk: Double? = null,
    @SerialName("hours")
    val hours: List<VCForecastBlock>? = null,
)

@Serializable
internal data class VCAlert(
    @SerialName("event")
    val event: String,
    @SerialName("description")
    val description: String,
)

private val logger = Logger.withTag("VCForecastResponse")

internal fun VCForecastResponse.toModel(
    nowProvider: NowProvider,
    maxDays: Int,
): Forecast {
    val todayBlock = days.firstOrNull()
        ?: error("There was no forecast for today!")

    // We want the next five hours from today, filter out all before and after
    val nowEpoch = nowProvider.now().epochSeconds
    val filteredHours = todayBlock.hours?.takeIf { it.isNotEmpty() }?.let { hours ->
        // Find the first hour block at or after the current time and take 5
        val startIndex = hours
            .indexOfFirst { it.datetimeEpoch >= nowEpoch }
            .takeIf { it >= 0 } ?: 0
        hours.subList(startIndex, minOf(startIndex + 5, hours.size))
    } ?: emptyList()

    val today = ForecastDay(
        block = todayBlock.toModel(),
        hours = filteredHours.toModels(),
    )

    val days = days.drop(1).take(maxDays).map { dayBlock ->
        val hours = dayBlock.hours.toModels()
        ForecastDay(block = dayBlock.toModel(), hours = hours)
    }

    return Forecast(
        location = Location(latitude = latitude, longitude = longitude, name = address),
        current = currentConditions.toModel(),
        today = today,
        days = days,
        alerts = alerts.map { alert -> Alert(title = alert.event, description = alert.description) },
        instant = nowProvider.now(),
    )
}

private fun List<VCForecastBlock>?.toModels(): List<ForecastBlock> = this?.map { it.toModel() } ?: emptyList()

private fun VCForecastBlock.toModel(): ForecastBlock =
    ForecastBlock(
        instant = Instant.fromEpochSeconds(datetimeEpoch),
        humidity = humidity,
        cloudCoverPercent = cloudCover.toInt(),
        temperature = Temperature(
            value = temp,
            feelsLike = feelsLike,
            max = tempMax,
            min = tempMin,
        ),
        precipitation = Precipitation(
            amount = precip ?: 0.0,
            probability = precipProb?.toInt() ?: 0,
            types = parsePrecipTypes(precipType),
        ),
        wind = Wind(
            speed = windSpeed,
            gust = windGust,
            directionDegree = windDir,
            maxSpeed = windSpeedMax,
            meanSpeed = windSpeedMean,
            minSpeed = windSpeedMin,
        ),
        pressure = pressure,
        uvIndex = uvIndex.toInt(),
        visibility = visibility,
        severeWeatherRisk = parseSevereWeatherRisk(severeRisk),
    )

private fun parsePrecipTypes(types: List<String>?) =
    types
        ?.mapNotNull { type ->
            when (type) {
                "rain" -> PrecipitationType.Rain
                "snow" -> PrecipitationType.Snow
                "freezingrain" -> PrecipitationType.FreezingRain
                "ice" -> PrecipitationType.Hail
                else -> null
            }
        }?.toSet() ?: emptySet()

private fun parseSevereWeatherRisk(risk: Double?): SevereWeatherRisk {
    if (risk == null) return SevereWeatherRisk.None
    return when (risk.roundToInt()) {
        in 0..30 -> SevereWeatherRisk.Low
        in 31..70 -> SevereWeatherRisk.Moderate
        else -> SevereWeatherRisk.High
    }
}
