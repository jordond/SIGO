package app.sigot.forecast.data.source.visualcrossing

internal interface VisualCrossingApi {
    suspend fun forecastFor(
        latitude: Double,
        longitude: Double,
    ): VCForecastResponse

    suspend fun forecastFor(name: String): VCForecastResponse
}
