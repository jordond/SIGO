package now.shouldigooutside.core.model.forecast

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.preferences.Activity
import kotlin.time.Instant

@Immutable
public sealed interface WeatherBannerInfo {
    /** Current score is Yes — user should go now, weather ends at [endsAt] because of [reason]. */
    public data class GoNow(
        val endsAt: Instant,
        val reason: WeatherReason?,
        val activity: Activity,
    ) : WeatherBannerInfo

    /** Current score is not Yes — show the next [window] of [quality]. */
    public data class NextWindow(
        val window: WeatherWindow,
        val quality: WindowQuality,
    ) : WeatherBannerInfo

    /** Current score is not Yes and no Yes or Maybe window exists in today's remaining hours. */
    public data object NoWindowToday : WeatherBannerInfo
}

public enum class WindowQuality {
    /** All hours in the window are [now.shouldigooutside.core.model.score.ScoreResult.Yes]. */
    Good,

    /** All hours in the window are [now.shouldigooutside.core.model.score.ScoreResult.Maybe]. */
    Borderline,
}

public enum class WeatherReason {
    Wind,
    Temperature,
    Precipitation,
    SevereWeather,
    AirQuality,
}
