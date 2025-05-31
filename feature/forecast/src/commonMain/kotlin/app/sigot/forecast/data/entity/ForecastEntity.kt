package app.sigot.forecast.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ForecastEntity(
    @SerialName("location_lat")
    public val locationLat: Double,
    @SerialName("location_long")
    public val locationLong: Double,
    @SerialName("location_name")
    public val locationName: String,
    @SerialName("current")
    public val current: ForecastBlockEntity,
    @SerialName("today")
    public val today: ForecastDayEntity,
    @SerialName("daily")
    public val daily: List<ForecastDayEntity>,
    @SerialName("alerts")
    public val alerts: List<AlertEntity>,
    @SerialName("updated_at")
    public val updatedAt: Long,
)

@Serializable
public data class ForecastDayEntity(
    @SerialName("block")
    public val block: ForecastBlockEntity,
    @SerialName("hours")
    public val hours: List<ForecastBlockEntity>,
)

@Serializable
public data class ForecastBlockEntity(
    @SerialName("instant")
    public val instant: Long,
    @SerialName("humidity")
    public val humidity: Double,
    @SerialName("cloud_cover_percent")
    public val cloudCoverPercent: Int,
    @SerialName("temperature")
    public val temperature: TemperatureEntity,
    @SerialName("precipitation")
    public val precipitation: PrecipitationEntity,
    @SerialName("wind")
    public val wind: WindEntity,
    @SerialName("pressure")
    public val pressure: Double,
    @SerialName("uv_index")
    public val uvIndex: Int,
    @SerialName("visibility")
    public val visibility: Double,
    @SerialName("severe_weather_risk")
    public val severeWeatherRisk: String,
)

@Serializable
public data class TemperatureEntity(
    @SerialName("value")
    public val value: Double,
    @SerialName("feels_like")
    public val feelsLike: Double,
    @SerialName("max")
    public val max: Double,
    @SerialName("min")
    public val min: Double,
)

@Serializable
public data class PrecipitationEntity(
    @SerialName("value")
    public val amount: Double,
    @SerialName("probability")
    public val probability: Int,
    @SerialName("types")
    public val types: List<String>,
)

@Serializable
public data class WindEntity(
    @SerialName("speed")
    public val speed: Double,
    @SerialName("gust")
    public val gust: Double,
    @SerialName("direction_degree")
    public val directionDegree: Double,
    @SerialName("max_speed")
    public val maxSpeed: Double,
    @SerialName("mean_speed")
    public val meanSpeed: Double,
    @SerialName("min_speed")
    public val minSpeed: Double,
)

@Serializable
public data class AlertEntity(
    @SerialName("title")
    public val title: String,
    @SerialName("description")
    public val description: String,
)
