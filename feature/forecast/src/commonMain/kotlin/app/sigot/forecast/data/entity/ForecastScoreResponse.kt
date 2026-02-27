package app.sigot.forecast.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ForecastScoreResponse(
    @SerialName("forecast")
    val forecast: ForecastEntity,
    @SerialName("score")
    val score: ForecastScoreEntity,
)
