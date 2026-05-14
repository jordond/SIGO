package now.shouldigooutside.core.widget

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

class WidgetInputsSerializationTest {
    @Test
    fun legacyStoredPreferencesWithIntegerValuesDecodesIntoDoubles() {
        // Pre-Double migration, minTemperature/maxTemperature/windSpeed were Int.
        // The iOS widget extension reads this JSON from the shared app group; existing
        // payloads on disk use bare integer literals. Verify they decode into Double fields.
        val legacyJson =
            """
            {
              "location": null,
              "units": {
                "temperature": "Celsius",
                "precipitation": "Millimeters",
                "windSpeed": "KilometersPerHour",
                "pressure": "Hectopascal"
              },
              "widgetActivityKey": "General",
              "preferences": {
                "minTemperature": 5,
                "maxTemperature": 35,
                "includeApparentTemperature": false,
                "windSpeed": 30,
                "rain": false,
                "snow": false,
                "maxAqi": 3
              },
              "includeAirQuality": true
            }
            """.trimIndent()

        val inputs = Json.decodeFromString<WidgetInputs>(legacyJson)

        inputs.preferences.minTemperature shouldBe 5.0
        inputs.preferences.maxTemperature shouldBe 35.0
        inputs.preferences.windSpeed shouldBe 30.0
    }
}
