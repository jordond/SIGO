package app.sigot.forecast.data.entity

import kotlinx.serialization.Serializable

@Serializable
public data class ForecastResponse(
    val forecast: ForecastEntity,
)
