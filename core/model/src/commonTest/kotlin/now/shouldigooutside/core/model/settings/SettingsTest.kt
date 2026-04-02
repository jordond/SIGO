package now.shouldigooutside.core.model.settings

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.collections.immutable.persistentMapOf
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class SettingsTest {
    private val defaultSettings = Settings(firstLaunch = Instant.fromEpochSeconds(0))

    @Test
    fun locationReturnsLastLocationWhenUseCustomLocationIsFalse() {
        val lastLocation = Location(1.0, 2.0, "Last")
        val settings = defaultSettings.copy(
            lastLocation = lastLocation,
            customLocation = Location(3.0, 4.0, "Custom"),
            useCustomLocation = false,
        )

        settings.location shouldBe lastLocation
    }

    @Test
    fun locationReturnsCustomLocationWhenUseCustomLocationIsTrue() {
        val customLocation = Location(3.0, 4.0, "Custom")
        val settings = defaultSettings.copy(
            lastLocation = Location(1.0, 2.0, "Last"),
            customLocation = customLocation,
            useCustomLocation = true,
        )

        settings.location shouldBe customLocation
    }

    @Test
    fun locationReturnsNullWhenNoLocationSet() {
        val settings = defaultSettings.copy(
            lastLocation = null,
            useCustomLocation = false,
        )

        settings.location shouldBe null
    }

    @Test
    fun addPutsActivityIntoMap() {
        val prefs = Preferences.defaultFor(Activity.Running)

        val updated = defaultSettings.add(Activity.Running, prefs)

        updated.activities[Activity.Running] shouldBe prefs
    }

    @Test
    fun addOverwritesExistingActivity() {
        val initial = defaultSettings.add(Activity.Running, Preferences.default)
        val newPrefs = Preferences.default.copy(windSpeed = 99)

        val updated = initial.add(Activity.Running, newPrefs)

        updated.activities[Activity.Running] shouldBe newPrefs
    }

    @Test
    fun removeGeneralReturnsUnchangedSettings() {
        val result = defaultSettings.remove(Activity.General)

        result shouldBeSameInstanceAs defaultSettings
    }

    @Test
    fun removeAbsentActivityReturnsUnchangedSettings() {
        val result = defaultSettings.remove(Activity.Running)

        result shouldBeSameInstanceAs defaultSettings
    }

    @Test
    fun removePresentActivityRemovesItFromMap() {
        val settings = defaultSettings.add(Activity.Running, Preferences.defaultFor(Activity.Running))

        val result = settings.remove(Activity.Running)

        result.activities.containsKey(Activity.Running) shouldBe false
    }

    @Test
    fun removeSelectedActivityResetsSelectionToGeneral() {
        val settings = defaultSettings
            .add(Activity.Running, Preferences.defaultFor(Activity.Running))
            .copy(selectedActivity = Activity.Running)

        val result = settings.remove(Activity.Running)

        result.selectedActivity shouldBe Activity.General
    }

    @Test
    fun removeNonSelectedActivityKeepsSelectionUnchanged() {
        val settings = defaultSettings
            .add(Activity.Running, Preferences.defaultFor(Activity.Running))
            .add(Activity.Cycling, Preferences.defaultFor(Activity.Cycling))
            .copy(selectedActivity = Activity.Cycling)

        val result = settings.remove(Activity.Running)

        result.selectedActivity shouldBe Activity.Cycling
    }

    @Test
    fun updatePreferencesWithSameValueReturnsSameInstance() {
        val result = defaultSettings.updatePreferences(Activity.General, Preferences.default)

        result shouldBeSameInstanceAs defaultSettings
    }

    @Test
    fun updatePreferencesWithDifferentValueUpdatesMap() {
        val newPrefs = Preferences.default.copy(windSpeed = 42)

        val result = defaultSettings.updatePreferences(Activity.General, newPrefs)

        result.activities[Activity.General] shouldBe newPrefs
        result shouldNotBe defaultSettings
    }

    @Test
    fun updatePreferencesForAbsentActivityAddsIt() {
        val newPrefs = Preferences.default.copy(windSpeed = 42)

        val result = defaultSettings.updatePreferences(Activity.Running, newPrefs)

        result.activities[Activity.Running] shouldBe newPrefs
    }

    @Test
    fun updatePreferencesForAbsentActivityWithDefaultValueIsNoOp() {
        val result = defaultSettings.updatePreferences(Activity.Running, Preferences.default)

        result.activities.containsKey(Activity.Running) shouldBe false
    }

    @Test
    fun deprecatedPreferencesReturnsGeneralPreferences() {
        @Suppress("DEPRECATION")
        val result = defaultSettings.preferences

        result shouldBe Preferences.default
    }

    @Test
    fun deprecatedPreferencesThrowsWhenGeneralMissing() {
        val settings = Settings(
            firstLaunch = Instant.fromEpochSeconds(0),
            activities = persistentMapOf(Activity.Running to Preferences.default),
        )

        assertFailsWith<IllegalStateException> {
            @Suppress("DEPRECATION")
            settings.preferences
        }
    }
}
