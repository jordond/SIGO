package now.shouldigooutside.core.foundation.analytics

public interface AnalyticsLogger {
    /**
     * Provide a way to check if we can log or not.
     *
     * Defaults to `true` and the responsibility to check [shouldLog] is up to the implementation
     * of [AnalyticsLogger]. It should be checked in [log] or [endTimedEvent].
     */
    public fun shouldLog(): Boolean = true

    /**
     * Log a normal event.
     *
     * @param[event] String name of the event.
     * @param[params] Optional key-value pair of values to log with the [event].
     */
    public fun log(
        event: String,
        params: Map<String, String> = emptyMap(),
    )

    /**
     * Log a normal event.
     *
     * @param[event] String name of the event.
     * @param[params] Optional key-value pair of values to log with the [event].
     */
    public fun log(
        event: String,
        vararg params: Pair<String, String>,
    ): Unit = log(event, params.toMap())

    /**
     * Start a timed log event.
     *
     * **Note:** It is up to the implementation to handle how to time the event.
     *
     * @param[event] String name of the event.
     * @param[params] Optional key-value pair of values to log with the [event].
     */
    public fun startTimedEvent(
        event: String,
        params: Map<String, String> = emptyMap(),
    )

    /**
     * Start a timed log event.
     *
     * **Note:** It is up to the implementation to handle how to time the event.
     *
     * @param[event] String name of the event.
     * @param[params] Optional key-value pair of values to log with the [event].
     */
    public fun startTimedEvent(
        event: String,
        vararg params: Pair<String, String>,
    ): Unit = startTimedEvent(event, params.toMap())

    /**
     * End a timed log event.
     *
     * @param[event] String name of the event.
     * @param[params] Optional key-value pair of values to log with the [event].
     */
    public fun endTimedEvent(
        event: String,
        params: Map<String, String> = emptyMap(),
    )

    /**
     * End a timed log event.
     *
     * @param[event] String name of the event.
     * @param[params] Optional key-value pair of values to log with the [event].
     */
    public fun endTimedEvent(
        event: String,
        vararg params: Pair<String, String>,
    ): Unit = endTimedEvent(event, params.toMap())

    /**
     * Normalize a parameter's [Int] value so that it can be logged.
     */
    public fun Int.groupValueToRange(): String =
        when (this) {
            in Int.MIN_VALUE..0 -> "0"
            in 1..100 -> "1-100"
            in 101..500 -> "100-500"
            in 501..1000 -> "500-1000"
            in 1001..2000 -> "1000-2000"
            in 2001..5000 -> "2000-5000"
            in 5001..10_000 -> "5000-10,000"
            else -> "> 10,000"
        }

    public fun Long.timeRange(): String =
        when (this) {
            in Long.MIN_VALUE..0 -> "0ms"
            in 1..300 -> "0-300ms"
            in 301..900 -> "300-900ms"
            in 901..1500 -> "900-1500ms"
            in 1500..3000 -> "1.5-3s"
            in 3001..5000 -> "3-5s"
            in 5000..10_000 -> "5-10s"
            in 10_000..60_000 -> "10-60s"
            in 60_001..300_000 -> "1-5m"
            in 300_001..600_000 -> "5-10m"
            in 600_001..900_000 -> "10-15m"
            in 900_001..1_200_000 -> "15-20m"
            else -> ">20m"
        }
}
