package app.sigot.forecast.domain.model

public data class Forecast(
    val location: Location,
)

public data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String,
)
