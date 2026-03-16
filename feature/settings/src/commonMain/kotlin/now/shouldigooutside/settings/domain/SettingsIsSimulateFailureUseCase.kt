package now.shouldigooutside.settings.domain

import now.shouldigooutside.core.domain.settings.IsSimulateFailureUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo

internal class SettingsIsSimulateFailureUseCase(
    private val settingsRepo: SettingsRepo,
) : IsSimulateFailureUseCase {
    override fun invoke(): Boolean = settingsRepo.settings.value.internalSettings.simulateFailure
}
