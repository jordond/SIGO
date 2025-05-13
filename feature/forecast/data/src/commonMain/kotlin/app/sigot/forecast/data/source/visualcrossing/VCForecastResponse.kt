package app.sigot.forecast.data.source.visualcrossing

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("tzoffset")
    val tzoffset: Double,
    @SerialName("days")
    val days: List<VCForecastDay>,
    @SerialName("alerts")
    val alerts: List<VCAlert> = emptyList(),
    @SerialName("currentConditions")
    val currentConditions: VCCurrentConditions? = null,
)

@Serializable
internal data class VCForecastDay(
    @SerialName("datetime")
    val datetime: String,
    @SerialName("datetimeEpoch")
    val datetimeEpoch: Long,
    @SerialName("tempmax")
    val tempMax: Double,
    @SerialName("tempmin")
    val tempMin: Double,
    @SerialName("temp")
    val temp: Double,
    @SerialName("feelslikemax")
    val feelsLikeMax: Double,
    @SerialName("feelslikemin")
    val feelsLikeMin: Double,
    @SerialName("feelslike")
    val feelsLike: Double,
    @SerialName("dew")
    val dew: Double,
    @SerialName("humidity")
    val humidity: Double,
    @SerialName("precip")
    val precip: Double,
    @SerialName("precipprob")
    val precipProb: Double,
    @SerialName("precipcover")
    val precipCover: Double? = null,
    @SerialName("preciptype")
    val precipType: List<String>? = null,
    @SerialName("snow")
    val snow: Double,
    @SerialName("snowdepth")
    val snowDepth: Double,
    @SerialName("windgust")
    val windGust: Double,
    @SerialName("windspeed")
    val windSpeed: Double,
    @SerialName("winddir")
    val windDir: Double,
    @SerialName("pressure")
    val pressure: Double,
    @SerialName("cloudcover")
    val cloudCover: Double,
    @SerialName("visibility")
    val visibility: Double,
    @SerialName("solarradiation")
    val solarRadiation: Double,
    @SerialName("solarenergy")
    val solarEnergy: Double,
    @SerialName("uvindex")
    val uvIndex: Double,
    @SerialName("severerisk")
    val severeRisk: Double,
    @SerialName("sunrise")
    val sunrise: String,
    @SerialName("sunriseEpoch")
    val sunriseEpoch: Long,
    @SerialName("sunset")
    val sunset: String,
    @SerialName("sunsetEpoch")
    val sunsetEpoch: Long,
    @SerialName("moonphase")
    val moonPhase: Double,
    @SerialName("conditions")
    val conditions: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("hours")
    val hours: List<VCForecastHour>,
)

@Serializable
internal data class VCForecastHour(
    @SerialName("datetime")
    val datetime: String,
    @SerialName("datetimeEpoch")
    val datetimeEpoch: Long,
    @SerialName("temp")
    val temp: Double,
    @SerialName("feelslike")
    val feelsLike: Double,
    @SerialName("humidity")
    val humidity: Double,
    @SerialName("dew")
    val dew: Double,
    @SerialName("precip")
    val precip: Double?,
    @SerialName("precipprob")
    val precipProb: Double,
    @SerialName("snow")
    val snow: Double?,
    @SerialName("snowdepth")
    val snowDepth: Double,
    @SerialName("preciptype")
    val precipType: List<String>? = null,
    @SerialName("windgust")
    val windGust: Double,
    @SerialName("windspeed")
    val windSpeed: Double,
    @SerialName("winddir")
    val windDir: Double,
    @SerialName("pressure")
    val pressure: Double,
    @SerialName("visibility")
    val visibility: Double,
    @SerialName("cloudcover")
    val cloudCover: Double,
    @SerialName("solarradiation")
    val solarRadiation: Double,
    @SerialName("solarenergy")
    val solarEnergy: Double,
    @SerialName("uvindex")
    val uvIndex: Double,
    @SerialName("severerisk")
    val severeRisk: Double,
    @SerialName("conditions")
    val conditions: String,
    @SerialName("icon")
    val icon: String,
)

@Serializable
internal data class VCAlert(
    @SerialName("event")
    val event: String,
    @SerialName("description")
    val description: String? = null,
)

@Serializable
internal data class VCCurrentConditions(
    @SerialName("datetime")
    val datetime: String,
    @SerialName("datetimeEpoch")
    val datetimeEpoch: Long,
    @SerialName("temp")
    val temp: Double,
    @SerialName("feelslike")
    val feelsLike: Double,
    @SerialName("humidity")
    val humidity: Double,
    @SerialName("dew")
    val dew: Double,
    @SerialName("precip")
    val precip: Double,
    @SerialName("precipprob")
    val precipProb: Double,
    @SerialName("snow")
    val snow: Double,
    @SerialName("snowdepth")
    val snowDepth: Double,
    @SerialName("preciptype")
    val precipType: List<String>? = null,
    @SerialName("windgust")
    val windGust: Double,
    @SerialName("windspeed")
    val windSpeed: Double,
    @SerialName("winddir")
    val windDir: Double,
    @SerialName("pressure")
    val pressure: Double,
    @SerialName("visibility")
    val visibility: Double,
    @SerialName("cloudcover")
    val cloudCover: Double,
    @SerialName("solarradiation")
    val solarRadiation: Double,
    @SerialName("solarenergy")
    val solarEnergy: Double,
    @SerialName("uvindex")
    val uvIndex: Double,
    @SerialName("conditions")
    val conditions: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("source")
    val source: String,
    @SerialName("sunrise")
    val sunrise: String,
    @SerialName("sunriseEpoch")
    val sunriseEpoch: Long,
    @SerialName("sunset")
    val sunset: String,
    @SerialName("sunsetEpoch")
    val sunsetEpoch: Long,
    @SerialName("moonphase")
    val moonPhase: Double,
)
