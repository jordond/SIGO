package app.sigot.settings.domain

import app.sigot.core.domain.settings.IsSimulateFailureUseCase
import app.sigot.core.domain.settings.SettingsRepo

internal class SettingsIsSimulateFailureUseCase(
    private val settingsRepo: SettingsRepo,
) : IsSimulateFailureUseCase {
    override fun invoke(): Boolean = settingsRepo.settings.value.internalSettings.simulateFailure
}
