package now.shouldigooutside.forecast.ui.activities.add

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences

@Immutable
internal sealed interface AddActivityAction {
    data class Select(
        val activity: Activity,
    ) : AddActivityAction

    data class Update(
        val preferences: Preferences,
    ) : AddActivityAction

    data object ResetPreferences : AddActivityAction

    data object Cancel : AddActivityAction

    data object Save : AddActivityAction
}
