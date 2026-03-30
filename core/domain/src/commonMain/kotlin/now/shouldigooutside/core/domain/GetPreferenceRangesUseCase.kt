package now.shouldigooutside.core.domain

import kotlinx.coroutines.flow.Flow
import now.shouldigooutside.core.model.preferences.PreferenceRanges

public interface GetPreferenceRangesUseCase {
    public val ranges: Flow<PreferenceRanges>

    public fun ranges(): PreferenceRanges
}
