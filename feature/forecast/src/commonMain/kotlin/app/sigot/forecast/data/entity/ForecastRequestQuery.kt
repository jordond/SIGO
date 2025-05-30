package app.sigot.forecast.data.entity

import kotlinx.serialization.Serializable

@Serializable
public data class ForecastRequestQuery(
    val lat: Double,
    val lon: Double,
    val name: String? = null,
)
