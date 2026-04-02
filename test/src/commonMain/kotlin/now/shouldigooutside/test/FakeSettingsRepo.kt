package now.shouldigooutside.test

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.settings.Settings
import kotlin.time.Instant

public class FakeSettingsRepo(
    initial: Settings = Settings(firstLaunch = Instant.fromEpochSeconds(0)),
) : SettingsRepo {
    private val _settings = MutableStateFlow(initial)
    override val settings: StateFlow<Settings> = _settings.asStateFlow()

    private val default: Settings = initial

    override fun update(block: (Settings) -> Settings) {
        _settings.value = block(_settings.value)
    }

    override fun reset() {
        _settings.value = default
    }
}
