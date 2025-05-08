package app.sigot.core.model.settings

import app.sigot.core.model.ui.ThemeMode
import kotlinx.datetime.Instant

public data class Settings(
    val firstLaunch: Instant,
    val themeMode: ThemeMode,
    val hasCompletedOnboarding: Boolean,
    val enableHaptics: Boolean,
    val internalSettings: InternalSettings,
    val loaded: Boolean,
)
