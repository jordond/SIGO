package now.shouldigooutside.settings.ui

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import now.shouldigooutside.core.config.AppConfigRepo
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.ui.ThemeMode
import org.jetbrains.compose.resources.getString

internal class SettingsModel(
    private val settingsRepo: SettingsRepo,
    private val appConfigRepo: AppConfigRepo,
) : UiStateViewModel<SettingsModel.State, SettingsModel.Event>(
        State(settingsRepo.settings.value),
    ) {
    init {
        settingsRepo.settings.mergeState { state, value -> state.copy(settings = value) }
    }

    fun updateTheme(mode: ThemeMode) {
        settingsRepo.update { settings -> settings.copy(themeMode = mode) }
    }

    fun toggleHaptics() {
        settingsRepo.update { settings -> settings.copy(enableHaptics = !settings.enableHaptics) }
    }

    fun toggle24HourFormat() {
        settingsRepo.update { settings -> settings.copy(use24HourFormat = !settings.use24HourFormat) }
    }

    fun clickAbout() {
        updateState { it.copy(aboutClicks = it.aboutClicks + 1) }
        Logger.d { "About tapped: ${state.value.aboutClicks} times." }
    }

    fun clickVersion() {
        updateState { it.copy(versionClicks = it.versionClicks + 1) }
        Logger.d { "Version tapped: ${state.value.versionClicks} times." }
    }

    fun enableInternalSettings() {
        if (state.value.settings.internalSettings.enabled) return

        if (state.value.canEnableInternalSettings) {
            settingsRepo.update { settings ->
                val internal = settings.internalSettings.copy(enabled = true)
                settings.copy(internalSettings = internal)
            }
        }
    }

    fun handleWebViewUrl(link: SettingsAction.WebViewUrl) {
        val urls = appConfigRepo.value.urlConfig
        val url = when (link) {
            is SettingsAction.OpenPrivacyPolicy -> urls.privacy
            is SettingsAction.OpenTermsAndConditions -> urls.terms
            is SettingsAction.OpenWebsite -> urls.root
        }

        viewModelScope.launch {
            val title = withContext(Dispatchers.Default) { getString(link.title) }
            emit(Event.OpenWebView(url, title))
        }
    }

    data class State(
        val settings: Settings,
        val aboutClicks: Int = 0,
        val versionClicks: Int = 0,
    ) {
        val canEnableInternalSettings: Boolean = !settings.internalSettings.enabled &&
            aboutClicks >= 5 &&
            versionClicks >= 5
    }

    sealed interface Event {
        data class OpenWebView(
            val title: String,
            val url: String,
        ) : Event
    }
}
