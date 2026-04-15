package now.shouldigooutside.core.widget

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.score.ScoreResult

@Immutable
@Serializable
public data class WidgetData(
    val scoreResult: ScoreResult,
    val scoreLabel: String,
    val locationName: String,
    val formattedTemp: String,
    val formattedFeelsLike: String,
    val formattedWind: String,
    val precipChance: Int,
    val todayScoreResult: ScoreResult,
    val todayScoreLabel: String,
    val alertCount: Int,
    val updatedAtMillis: Long,
    val isStale: Boolean,
    val updatedAgoLabel: String,
    val activityName: String? = null,
)
