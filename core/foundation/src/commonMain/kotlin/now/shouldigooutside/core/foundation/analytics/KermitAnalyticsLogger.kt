package now.shouldigooutside.core.foundation.analytics

import co.touchlab.kermit.Logger

/**
 * A debug-mode implementation of [AnalyticsLogger] that outputs to logcat.
 */
internal class KermitAnalyticsLogger(
    private val tag: String = TAG,
) : AnalyticsLogger {
    override fun log(
        event: String,
        params: Map<String, String>,
    ) {
        Logger.i(tag = tag) { "Logging Analytics Event:\nEvent -> $event \nParams ->\n${params.print()}" }
    }

    override fun startTimedEvent(
        event: String,
        params: Map<String, String>,
    ) {
        Logger.i(tag = tag) { "Timed Event Start: $event \n${params.print()}" }
    }

    override fun endTimedEvent(
        event: String,
        params: Map<String, String>,
    ) {
        Logger.i(tag = tag) { "Timed Event Stop:\nEvent -> $event \nParams ->\n${params.print()}" }
    }

    private fun Map<String, String>.print() = map { (key, value) -> "\t$key => $value" }.joinToString("\n")

    private companion object {
        const val TAG = "AnalyticsLogger"
    }
}
