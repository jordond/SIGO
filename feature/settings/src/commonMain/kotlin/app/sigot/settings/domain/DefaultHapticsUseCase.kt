package app.sigot.settings.domain

import app.sigot.core.domain.settings.HapticsUseCase
import app.sigot.core.domain.settings.SettingsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

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
