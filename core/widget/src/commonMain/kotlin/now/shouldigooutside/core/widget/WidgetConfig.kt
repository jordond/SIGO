package now.shouldigooutside.core.widget

import kotlinx.serialization.Serializable

@Serializable
public data class WidgetConfig(
    val backendUrl: String,
    val clientId: String,
    val lat: Double,
    val lon: Double,
    val locationName: String,
    val minTemp: Int,
    val maxTemp: Int,
    val maxWind: Int,
    val allowRain: Boolean,
    val allowSnow: Boolean,
    val maxAqi: Int,
    val includeAirQuality: Boolean,
    val activityName: String = WidgetData.DEFAULT_ACTIVITY_NAME,
)
