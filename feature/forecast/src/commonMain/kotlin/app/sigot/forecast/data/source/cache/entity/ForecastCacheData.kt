package app.sigot.forecast.data.source.cache.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ForecastEntity(
    @SerialName("location_lat")
    val locationLat: Double,
    @SerialName("location_long")
    val locationLong: Double,
    @SerialName("location_name")
    val locationName: String,
    @SerialName("current")
    val current: ForecastBlockEntity,
    @SerialName("today")
    val today: ForecastDayEntity,
    @SerialName("daily")
    val daily: List<ForecastDayEntity>,
    @SerialName("alerts")
    val alerts: List<AlertEntity>,
    @SerialName("updated_at")
    val updatedAt: Long,
)

@Serializable
internal data class ForecastDayEntity(
    @SerialName("block")
    val block: ForecastBlockEntity,
    @SerialName("hours")
    val hours: List<ForecastBlockEntity>,
)

@Serializable
internal data class ForecastBlockEntity(
    @SerialName("instant")
    val instant: Long,
    @SerialName("humidity")
    val humidity: Double,
    @SerialName("cloud_cover_percent")
    val cloudCoverPercent: Int,
    @SerialName("temperature")
    val temperature: TemperatureEntity,
    @SerialName("precipitation")
    val precipitation: PrecipitationEntity,
    @SerialName("wind")
    val wind: WindEntity,
    @SerialName("pressure")
    val pressure: Double,
    @SerialName("uv_index")
    val uvIndex: Int,
    @SerialName("visibility")
    val visibility: Double,
    @SerialName("severe_weather_risk")
    val severeWeatherRisk: String,
)

@Serializable
internal data class TemperatureEntity(
    @SerialName("value")
    val value: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("max")
    val max: Double,
    @SerialName("min")
    val min: Double,
)

@Serializable
internal data class PrecipitationEntity(
    @SerialName("value")
    val amount: Double,
    @SerialName("probability")
    val probability: Int,
    @SerialName("types")
    val types: List<String>,
)

@Serializable
internal data class WindEntity(
    @SerialName("speed")
    val speed: Double,
    @SerialName("gust")
    val gust: Double,
    @SerialName("direction_degree")
    val directionDegree: Double,
    @SerialName("max_speed")
    val maxSpeed: Double,
    @SerialName("mean_speed")
    val meanSpeed: Double,
    @SerialName("min_speed")
    val minSpeed: Double,
)

@Serializable
internal data class AlertEntity(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
)
