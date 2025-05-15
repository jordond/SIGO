package app.sigot.core.model.settings

import app.sigot.core.model.ui.ThemeMode
import app.sigot.core.model.units.Units
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

public data class Settings(
    val firstLaunch: Instant = Clock.System.now(),
    val themeMode: ThemeMode = ThemeMode.System,
    val hasCompletedOnboarding: Boolean = false,
    val units: Units = Units.Metric,
    val enableHaptics: Boolean = true,
    val internalSettings: InternalSettings = InternalSettings(),
    val loaded: Boolean = false,
)
