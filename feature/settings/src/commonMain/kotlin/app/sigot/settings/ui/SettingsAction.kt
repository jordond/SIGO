package app.sigot.settings.ui

import app.sigot.core.model.ui.ThemeMode
import app.sigot.core.resources.Res
import app.sigot.core.resources.url_title_privacy
import app.sigot.core.resources.url_title_terms
import app.sigot.core.resources.url_title_website
import org.jetbrains.compose.resources.StringResource

internal sealed interface SettingsAction {
    data object Close : SettingsAction

    data class UpdateTheme(
        val mode: ThemeMode,
    ) : SettingsAction

    data object ToggleHaptics : SettingsAction

    data object Toggle24HourFormat : SettingsAction

    data object ToUnitsScreen : SettingsAction

    data object ToPreferencesScreen : SettingsAction

    data object ShareApp : SettingsAction

    data object RateApp : SettingsAction

    data object TapAbout : SettingsAction

    data object TapVersion : SettingsAction

    sealed interface WebViewUrl : SettingsAction {
        val title: StringResource
    }

    data object OpenWebsite : SettingsAction, WebViewUrl {
        override val title = Res.string.url_title_website
    }

    data object OpenPrivacyPolicy : SettingsAction, WebViewUrl {
        override val title = Res.string.url_title_privacy
    }

    data object OpenTermsAndConditions : SettingsAction, WebViewUrl {
        override val title = Res.string.url_title_terms
    }
}
