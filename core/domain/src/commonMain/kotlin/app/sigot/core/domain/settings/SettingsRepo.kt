package app.sigot.core.domain.settings

import app.sigot.core.model.settings.Settings
import kotlinx.coroutines.flow.StateFlow

public interface SettingsRepo {
    public val settings: StateFlow<Settings>

    public fun update(block: (Settings) -> Settings)

    public fun update(settings: Settings) {
        update { settings }
    }

    public fun reset()
}
