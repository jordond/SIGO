package now.shouldigooutside.core.widget

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class WidgetData(
    @SerialName("scoreResult") val scoreResult: String,
    @SerialName("locationName") val locationName: String,
    @SerialName("currentTemp") val currentTemp: Double,
    @SerialName("tempUnit") val tempUnit: String,
    @SerialName("feelsLikeTemp") val feelsLikeTemp: Double,
    @SerialName("windSpeed") val windSpeed: Double,
    @SerialName("windSpeedUnit") val windSpeedUnit: String,
    @SerialName("precipChance") val precipChance: Int,
    @SerialName("todayScoreResult") val todayScoreResult: String,
    @SerialName("alertCount") val alertCount: Int,
    @SerialName("updatedAtMillis") val updatedAtMillis: Long,
)
