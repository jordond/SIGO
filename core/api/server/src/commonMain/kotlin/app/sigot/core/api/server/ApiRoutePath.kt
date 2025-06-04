package app.sigot.core.api.server

public enum class ApiRoutePath(
    public val path: String,
) {
    Version("/"),
    Forecast("/forecast"),
    ForecastScore("/forecast/score"),
}
