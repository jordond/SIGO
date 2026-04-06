package now.shouldigooutside.core.widget

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.WindSpeedUnit
import kotlin.math.roundToInt
import kotlin.time.Clock

@Immutable
@Serializable
public data class WidgetData(
    val scoreResult: ScoreResult,
    val locationName: String,
    val currentTemp: Double,
    val tempUnit: TemperatureUnit,
    val feelsLikeTemp: Double,
    val windSpeed: Double,
    val windSpeedUnit: WindSpeedUnit,
    val precipChance: Int,
    val todayScoreResult: ScoreResult,
    val alertCount: Int,
    val updatedAtMillis: Long,
    val activityName: String = DEFAULT_ACTIVITY_NAME,
) {
    public val formattedTemp: String
        get() = "${currentTemp.roundToInt()}°${tempUnit.name.first()}"

    public val formattedFeelsLike: String
        get() = "${feelsLikeTemp.roundToInt()}°${tempUnit.name.first()}"

    public val formattedWind: String
        get() = "${windSpeed.roundToInt()} ${windSpeedUnit.label}"

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
        public const val DEFAULT_ACTIVITY_NAME: String = "General"
        private const val STALE_THRESHOLD_MS: Long = 2 * 60 * 60 * 1000
    }
}

public val WindSpeedUnit.label: String
    get() = when (this) {
        WindSpeedUnit.MilePerHour -> "mph"
        WindSpeedUnit.KilometerPerHour -> "km/h"
        WindSpeedUnit.MeterPerSecond -> "m/s"
        WindSpeedUnit.Knot -> "kn"
    }
