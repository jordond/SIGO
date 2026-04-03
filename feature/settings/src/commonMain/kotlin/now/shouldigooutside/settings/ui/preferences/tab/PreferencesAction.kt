package now.shouldigooutside.settings.ui.preferences.tab

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences

@Immutable
public sealed interface PreferencesAction {
    public data class Select(
        val activity: Activity,
    ) : PreferencesAction

    public data class Update(
        val preferences: Preferences,
    ) : PreferencesAction

    public data object ResetPreferences : PreferencesAction

    public data object Delete : PreferencesAction

    public data object ToAddActivity : PreferencesAction

    public data object ToSettings : PreferencesAction
}
