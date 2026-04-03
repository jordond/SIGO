package now.shouldigooutside.core.widget

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.WindSpeedUnit

@Serializable
public data class WidgetData(
    @SerialName("scoreResult") val scoreResult: ScoreResult,
    @SerialName("locationName") val locationName: String,
    @SerialName("currentTemp") val currentTemp: Double,
    @SerialName("tempUnit") val tempUnit: TemperatureUnit,
    @SerialName("feelsLikeTemp") val feelsLikeTemp: Double,
    @SerialName("windSpeed") val windSpeed: Double,
    @SerialName("windSpeedUnit") val windSpeedUnit: WindSpeedUnit,
    @SerialName("precipChance") val precipChance: Int,
    @SerialName("todayScoreResult") val todayScoreResult: ScoreResult,
    @SerialName("alertCount") val alertCount: Int,
    @SerialName("updatedAtMillis") val updatedAtMillis: Long,
    @SerialName("activityName") val activityName: String = DEFAULT_ACTIVITY_NAME,
) {
    public companion object {
        public const val DEFAULT_ACTIVITY_NAME: String = "General"
    }
}
