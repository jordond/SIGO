package app.sigot.core.config

import app.sigot.core.config.model.AppConfig
import app.sigot.core.model.Initializable
import kotlinx.coroutines.flow.Flow

public interface AppConfigProvider : Initializable {
    public val appConfig: Flow<AppConfig>

    public override suspend fun initialize()
}
