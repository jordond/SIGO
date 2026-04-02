package now.shouldigooutside.settings.data.entity

import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentMapOf
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.core.model.units.Units
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class SettingsEntityMapperTest {
    private val baseInternalSettingsEntity = InternalSettingsEntity()

    @Test
    fun activityRoundTripGeneral() {
        Activity.General.toEntity().toModel() shouldBe Activity.General
    }

    @Test
    fun activityRoundTripWalking() {
        Activity.Walking.toEntity().toModel() shouldBe Activity.Walking
    }

    @Test
    fun activityRoundTripRunning() {
        Activity.Running.toEntity().toModel() shouldBe Activity.Running
    }

    @Test
    fun activityRoundTripCycling() {
        Activity.Cycling.toEntity().toModel() shouldBe Activity.Cycling
    }

    @Test
    fun activityRoundTripHiking() {
        Activity.Hiking.toEntity().toModel() shouldBe Activity.Hiking
    }

    @Test
    fun activityRoundTripSwimming() {
        Activity.Swimming.toEntity().toModel() shouldBe Activity.Swimming
    }

    @Test
    fun customActivityPreservesName() {
        val custom = Activity.Custom(name = "Skateboarding")

        val result = mapActivityEntityToModel(custom.toEntity())

        result shouldBe custom
    }

    @Test
    fun unknownActivityThrows() {
        assertFailsWith<IllegalStateException> {
            mapActivityEntityToModel("UnknownActivity")
        }
    }

    @Test
    fun preferencesWithNoUnitsUsesValuesAsIs() {
        val entity = PreferencesEntity(
            units = null,
            minTemperature = 5,
            maxTemperature = 30,
            includeApparentTemperature = true,
            windSpeed = 20,
            rain = true,
            snow = false,
            maxAqi = 3,
        )

        val result = entity.toModel()

        result.minTemperature shouldBe 5
        result.maxTemperature shouldBe 30
        result.includeApparentTemperature shouldBe true
        result.windSpeed shouldBe 20
        result.rain shouldBe true
        result.snow shouldBe false
        result.maxAqi shouldBe AirQuality(3)
    }

    @Test
    fun preferencesWithUnitsConvertsToMetric() {
        // 32°F = 0°C, 50 mph ≈ 80 km/h
        val imperialUnitsEntity = Units.Imperial.toEntity()
        val entity = PreferencesEntity(
            units = imperialUnitsEntity,
            minTemperature = 32, // 32°F = 0°C
            maxTemperature = 32,
            includeApparentTemperature = false,
            windSpeed = 0,
            rain = false,
            snow = false,
            maxAqi = 2,
        )

        val result = entity.toModel()

        result.minTemperature shouldBe 0 // converted from Fahrenheit to Celsius
        result.maxTemperature shouldBe 0
    }

    @Test
    fun preferencesRoundTrip() {
        val preferences = Preferences.default

        val result = preferences.toEntity().toModel()

        result shouldBe preferences
    }

    @Test
    fun settingsOldPreferencesMigrated() {
        val oldPreferences = PreferencesEntity(
            units = null,
            minTemperature = 10,
            maxTemperature = 28,
            includeApparentTemperature = false,
            windSpeed = 25,
            rain = true,
            snow = false,
            maxAqi = 3,
        )
        val entity = SettingsEntity(
            firstLaunch = 0L,
            theme = "Light",
            hasCompletedOnboarding = false,
            preferences = oldPreferences,
            activities = emptyMap(),
            internalSettings = baseInternalSettingsEntity,
        )

        val result = entity.toModel()

        result.activities[Activity.General] shouldBe oldPreferences.toModel()
    }

    @Test
    fun settingsOldUnitsUsedWhenPresent() {
        val imperialUnitsEntity = Units.Imperial.toEntity()
        val oldPreferences = PreferencesEntity(
            units = imperialUnitsEntity,
            minTemperature = 32,
            maxTemperature = 104,
            includeApparentTemperature = false,
            windSpeed = 0,
            rain = false,
            snow = false,
            maxAqi = 2,
        )
        val entity = SettingsEntity(
            firstLaunch = 0L,
            theme = "Light",
            hasCompletedOnboarding = false,
            preferences = oldPreferences,
            activities = emptyMap(),
            internalSettings = baseInternalSettingsEntity,
        )

        val result = entity.toModel()

        // Old units stored in preferences are used to derive the root units
        result.units shouldBe Units.Imperial
    }

    @Test
    fun settingsMissingSelectedActivityFallsBackToGeneral() {
        val activities = mapOf(
            ActivityEntity.General to PreferencesEntity(
                units = null,
                minTemperature = 5,
                maxTemperature = 35,
                includeApparentTemperature = false,
                windSpeed = 30,
                rain = false,
                snow = false,
                maxAqi = 3,
            ),
        )
        val entity = SettingsEntity(
            firstLaunch = 0L,
            theme = "Light",
            hasCompletedOnboarding = false,
            selectedActivity = ActivityEntity.Walking, // Walking not in activities map
            activities = activities,
            internalSettings = baseInternalSettingsEntity,
        )

        val result = entity.toModel()

        result.selectedActivity shouldBe Activity.General
    }

    @Test
    fun settingsNewFormatUsedDirectly() {
        val walkingPreferences = PreferencesEntity(
            units = null,
            minTemperature = -10,
            maxTemperature = 30,
            includeApparentTemperature = true,
            windSpeed = 35,
            rain = true,
            snow = true,
            maxAqi = 5,
        )
        val activities = mapOf(
            ActivityEntity.General to PreferencesEntity(
                units = null,
                minTemperature = 5,
                maxTemperature = 35,
                includeApparentTemperature = false,
                windSpeed = 30,
                rain = false,
                snow = false,
                maxAqi = 3,
            ),
            ActivityEntity.Walking to walkingPreferences,
        )
        val entity = SettingsEntity(
            firstLaunch = 0L,
            theme = "Dark",
            hasCompletedOnboarding = true,
            selectedActivity = ActivityEntity.Walking,
            activities = activities,
            internalSettings = baseInternalSettingsEntity,
        )

        val result = entity.toModel()

        result.selectedActivity shouldBe Activity.Walking
        result.themeMode shouldBe ThemeMode.Dark
        result.hasCompletedOnboarding shouldBe true
        result.activities[Activity.Walking] shouldBe walkingPreferences.toModel()
    }

    @Test
    fun settingsRoundTrip() {
        val settings = Settings(
            firstLaunch = Instant.fromEpochSeconds(0),
            themeMode = ThemeMode.Light,
            hasCompletedOnboarding = true,
            units = Units.Metric,
            selectedActivity = Activity.General,
            activities = persistentMapOf(Activity.General to Preferences.default),
            use24HourFormat = false,
            includeAirQuality = true,
            enableActivities = true,
            enableHaptics = true,
            loaded = true,
        )

        val result = settings.toEntity().toModel()

        result.firstLaunch shouldBe settings.firstLaunch
        result.themeMode shouldBe settings.themeMode
        result.hasCompletedOnboarding shouldBe settings.hasCompletedOnboarding
        result.units shouldBe settings.units
        result.selectedActivity shouldBe settings.selectedActivity
        result.activities shouldBe settings.activities
        result.use24HourFormat shouldBe settings.use24HourFormat
        result.includeAirQuality shouldBe settings.includeAirQuality
        result.enableActivities shouldBe settings.enableActivities
        result.enableHaptics shouldBe settings.enableHaptics
    }

    private fun String.toModel(): Activity = mapActivityEntityToModel(this)
}
