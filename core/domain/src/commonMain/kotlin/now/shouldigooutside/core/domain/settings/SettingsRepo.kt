package now.shouldigooutside.core.domain.settings

import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.core.model.settings.Settings

public interface SettingsRepo {
    public val settings: StateFlow<Settings>

    public fun update(block: (Settings) -> Settings)

    public fun update(settings: Settings) {
        update { settings }
    }

    public fun reset()
}
