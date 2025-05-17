package app.sigot.core.model.settings

import app.sigot.core.model.forecast.Location
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.ui.ThemeMode
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

public data class Settings(
    val firstLaunch: Instant = Clock.System.now(),
    val themeMode: ThemeMode = ThemeMode.System,
    val hasCompletedOnboarding: Boolean = false,
    val lastLocation: Location? = null,
    val preferences: Preferences = Preferences.default,
    val enableHaptics: Boolean = true,
    val internalSettings: InternalSettings = InternalSettings(),
    val loaded: Boolean = false,
)
