package app.sigot.core.config

import app.sigot.core.config.model.AppConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

public interface AppConfigRepo {
    public val config: StateFlow<AppConfig>

    public val value: AppConfig
        get() = config.value
}

internal class DefaultAppConfigRepo(
    provider: AppConfigProvider,
    coroutineScope: CoroutineScope,
) : AppConfigRepo {
    override val config = provider.appConfig.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = AppConfig(),
    )
}
