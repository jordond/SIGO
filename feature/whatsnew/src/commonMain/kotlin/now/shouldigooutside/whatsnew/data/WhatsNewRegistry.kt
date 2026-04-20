package now.shouldigooutside.whatsnew.data

import androidx.compose.ui.layout.ContentScale
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.whats_new_activities
import now.shouldigooutside.core.resources.whats_new_activities_desc
import now.shouldigooutside.core.resources.whats_new_air_quality
import now.shouldigooutside.core.resources.whats_new_air_quality_desc
import now.shouldigooutside.core.resources.whats_new_aqi
import now.shouldigooutside.core.resources.whats_new_metrics
import now.shouldigooutside.core.resources.whats_new_weather_metrics
import now.shouldigooutside.core.resources.whats_new_weather_metrics_desc
import now.shouldigooutside.core.resources.whats_new_weather_window
import now.shouldigooutside.core.resources.whats_new_weather_window_desc
import now.shouldigooutside.core.resources.whats_new_window

/**
 * Flat list of all What's New pages. Add new pages at the top (newest first).
 * See `docs/whats-new.md` for details.
 */
public object WhatsNewRegistry {
    public val pages: List<WhatsNewPage> = listOf(
        WhatsNewPage(
            version = 15,
            title = Res.string.whats_new_weather_metrics,
            description = Res.string.whats_new_weather_metrics_desc,
            image = Res.drawable.whats_new_metrics,
            scale = ContentScale.Fit,
        ),
        WhatsNewPage(
            version = 14,
            title = Res.string.whats_new_activities,
            description = Res.string.whats_new_activities_desc,
            image = Res.drawable.whats_new_activities,
        ),
        WhatsNewPage(
            version = 14,
            title = Res.string.whats_new_air_quality,
            description = Res.string.whats_new_air_quality_desc,
            image = Res.drawable.whats_new_aqi,
        ),
        WhatsNewPage(
            version = 14,
            title = Res.string.whats_new_weather_window,
            description = Res.string.whats_new_weather_window_desc,
            image = Res.drawable.whats_new_window,
            scale = ContentScale.Fit,
        ),
    )
}
