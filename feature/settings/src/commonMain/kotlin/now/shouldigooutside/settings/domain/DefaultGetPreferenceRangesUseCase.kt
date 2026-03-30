package now.shouldigooutside.settings.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import now.shouldigooutside.core.domain.GetPreferenceRangesUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.PreferenceRanges

internal class DefaultGetPreferenceRangesUseCase(
    private val settingsRepo: SettingsRepo,
) : GetPreferenceRangesUseCase {
    override val ranges: Flow<PreferenceRanges> = settingsRepo.settings
        .map { it.units }
        .distinctUntilChanged()
        .map { PreferenceRanges.from(it) }
        .distinctUntilChanged()

    override fun ranges(): PreferenceRanges = PreferenceRanges.from(settingsRepo.settings.value.units)
}
