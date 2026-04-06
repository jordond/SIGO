package now.shouldigooutside.core.widget

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.score.ScoreResult
import kotlin.time.Clock

@Immutable
@Serializable
public data class WidgetData(
    val scoreResult: ScoreResult,
    val locationName: String,
    val formattedTemp: String,
    val formattedFeelsLike: String,
    val formattedWind: String,
    val precipChance: Int,
    val todayScoreResult: ScoreResult,
    val alertCount: Int,
    val updatedAtMillis: Long,
    val activityName: String? = null,
) {
    val showActivityName: Boolean get() = activityName != null

    public val isStale: Boolean
        get() {
            val nowMs = Clock.System.now().toEpochMilliseconds()
            return (nowMs - updatedAtMillis) > STALE_THRESHOLD_MS
        }

    public val updatedAgoMinutes: Long
        get() {
            val nowMs = Clock.System.now().toEpochMilliseconds()
            return (nowMs - updatedAtMillis) / 60_000
        }

    public companion object {
        private const val STALE_THRESHOLD_MS: Long = 2 * 60 * 60 * 1000
    }
}
