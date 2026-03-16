package now.shouldigooutside.cli.config

import now.shouldigooutside.core.platform.store.Store

interface CliConfigRepo {
    suspend fun get(): CliConfig?

    suspend fun set(config: CliConfig)

    suspend fun update(block: (CliConfig) -> CliConfig)

    suspend fun reset()
}

internal class DefaultCliConfigRepo(
    private val store: Store<CliConfig>,
) : CliConfigRepo {
    override suspend fun get(): CliConfig? = store.get()

    override suspend fun set(config: CliConfig): Unit = store.set(config)

    override suspend fun update(block: (CliConfig) -> CliConfig) {
        store.update { block(it ?: CliConfig()) }
    }

    override suspend fun reset() {
        store.clear()
    }
}
