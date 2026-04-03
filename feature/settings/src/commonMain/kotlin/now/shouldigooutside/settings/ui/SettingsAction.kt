package now.shouldigooutside.settings.ui

import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.url_title_privacy
import now.shouldigooutside.core.resources.url_title_terms
import now.shouldigooutside.core.resources.url_title_website
import org.jetbrains.compose.resources.StringResource

internal sealed interface SettingsAction {
    data object Close : SettingsAction

    data class UpdateTheme(
        val mode: ThemeMode,
    ) : SettingsAction

    data object ToggleHaptics : SettingsAction

    data object Toggle24HourFormat : SettingsAction

    data object ToggleAirQuality : SettingsAction

    data object ToggleActivities : SettingsAction

    data object ToggleRememberActivity : SettingsAction

    data object ToUnitsScreen : SettingsAction

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
