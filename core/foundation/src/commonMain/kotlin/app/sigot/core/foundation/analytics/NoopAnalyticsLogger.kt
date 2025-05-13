package app.sigot.core.foundation.analytics

internal class NoopAnalyticsLogger : AnalyticsLogger {
    override fun log(
        event: String,
        params: Map<String, String>,
    ) {
    }

    override fun startTimedEvent(
        event: String,
        params: Map<String, String>,
    ) {
    }

    override fun endTimedEvent(
        event: String,
        params: Map<String, String>,
    ) {
    }
}
