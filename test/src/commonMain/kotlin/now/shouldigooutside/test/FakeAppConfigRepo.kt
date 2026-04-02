package now.shouldigooutside.test

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import now.shouldigooutside.core.config.AppConfigRepo
import now.shouldigooutside.core.config.model.AppConfig

public class FakeAppConfigRepo(
    initial: AppConfig = AppConfig(),
) : AppConfigRepo {
    private val _config = MutableStateFlow(initial)
    override val config: StateFlow<AppConfig> = _config.asStateFlow()

    public fun update(block: (AppConfig) -> AppConfig) {
        _config.value = block(_config.value)
    }

    public fun set(config: AppConfig) {
        _config.value = config
    }
}
