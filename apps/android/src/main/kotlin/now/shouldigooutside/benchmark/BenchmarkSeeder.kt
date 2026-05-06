package now.shouldigooutside.benchmark

import android.content.Context
import java.io.File

internal class BenchmarkSeeder(
    private val context: Context,
) {
    fun seed() {
        if (!hasFixture()) return
        writeSettings()
        writeForecastCache()
    }

    private fun hasFixture(): Boolean =
        runCatching {
            context.assets.list("")?.contains(FIXTURE_FILE) == true
        }.getOrDefault(false)

    private fun writeSettings() {
        val now = System.currentTimeMillis()
        val json =
            """
            {
              "first_launch": $now,
              "theme": "System",
              "has_completed_onboarding": true,
              "last_location": {
                "latitude": 40.7128,
                "longitude": -74.0060,
                "name": "New York",
                "administrativeArea": "New York",
                "country": "United States"
              },
              "use_custom_location": false,
              "use_24_hour_format": false,
              "include_air_quality": true,
              "enable_activities": true,
              "remember_activity": true,
              "selected_activity": "General",
              "widget_activity": "General",
              "activities": {},
              "enable_haptics": true,
              "internal_settings": {}
            }
            """.trimIndent()
        File(context.filesDir, "app-settings.json").writeText(json)
    }

    private fun writeForecastCache() {
        val target = File(context.cacheDir, "forecast_cache.json")
        context.assets.open(FIXTURE_FILE).use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
    }

    private companion object {
        const val FIXTURE_FILE = "forecast_fixture.json"
    }
}
