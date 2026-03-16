package now.shouldigooutside.settings.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import now.shouldigooutside.core.domain.settings.HapticsUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo

internal class DefaultHapticsUseCase(
    private val settingsRepo: SettingsRepo,
) : HapticsUseCase {
    override fun isEnabled(): Boolean = settingsRepo.settings.value.enableHaptics

    override fun updates(): Flow<Boolean> =
        settingsRepo.settings.map { it.enableHaptics }.distinctUntilChanged()

    override fun update(value: Boolean) {
        settingsRepo.update { it.copy(enableHaptics = value) }
    }
}
