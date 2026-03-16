package app.sigot.core.api.model

public enum class ApiRoutePath(
    public val path: String,
) {
    Version("/"),
    Forecast("/forecast"),
    ForecastScore("/forecast/score"),
}
