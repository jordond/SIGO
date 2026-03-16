package now.shouldigooutside.core.config

import kotlinx.coroutines.flow.Flow
import now.shouldigooutside.core.config.model.AppConfig
import now.shouldigooutside.core.model.Initializable

public interface AppConfigProvider : Initializable {
    public val appConfig: Flow<AppConfig>

    public override suspend fun initialize()
}
