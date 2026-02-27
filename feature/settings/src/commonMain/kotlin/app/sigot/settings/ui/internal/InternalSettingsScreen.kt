package app.sigot.settings.ui.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.settings.InternalSettings
import app.sigot.core.resources.Res
import app.sigot.core.resources.save
import app.sigot.core.resources.settings_internal_backend
import app.sigot.core.resources.settings_internal_backend_api
import app.sigot.core.resources.settings_internal_backend_api_desc
import app.sigot.core.resources.settings_internal_backend_direct_api
import app.sigot.core.resources.settings_internal_backend_direct_api_desc
import app.sigot.core.resources.settings_internal_backend_direct_api_token_placeholder
import app.sigot.core.resources.settings_internal_backend_simulate_failure
import app.sigot.core.resources.settings_internal_backend_simulate_failure_desc
import app.sigot.core.resources.settings_internal_reset
import app.sigot.core.resources.settings_internal_title
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Switch
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.textfield.TextField
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Check
import app.sigot.core.ui.icons.lucide.Link
import app.sigot.core.ui.icons.lucide.Server
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import app.sigot.settings.ui.components.SettingsCard
import app.sigot.settings.ui.components.SettingsTextRow
import app.sigot.settings.ui.components.SettingsTopBar
import dev.stateholder.extensions.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun InternalSettingsScreen(
    onBack: () -> Unit,
    model: InternalSettingsModel = koinViewModel(),
) {
    val state by model.collectAsState()

    InternalSettingsScreen(
        settings = state.settings.internalSettings,
        update = model::update,
        onBack = onBack,
    )
}

@Composable
internal fun InternalSettingsScreen(
    settings: InternalSettings,
    modifier: Modifier = Modifier,
    update: (InternalSettings) -> Unit,
    onBack: () -> Unit = {},
) {
    var backendApiUrl by remember(settings.backendApiUrl) { mutableStateOf(settings.backendApiUrl) }
    val backendApiUrlChanged = remember(backendApiUrl, settings.backendApiUrl) {
        backendApiUrl != settings.backendApiUrl && backendApiUrl.isNotBlank()
    }

    var apiKey by remember(settings.apiKey) { mutableStateOf(settings.apiKey) }
    val apiKeyChanged = remember(apiKey, settings.apiKey) {
        apiKey != settings.apiKey && !apiKey.isNullOrBlank()
    }

    Scaffold(
        modifier = modifier,
        containerColor = AppTheme.colors.surface,
        topBar = {
            SettingsTopBar(
                text = Res.string.settings_internal_title,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.large),
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                ).padding(horizontal = AppTheme.spacing.standard)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(4.dp))

            SettingsCard(
                text = Res.string.settings_internal_backend,
                colors = CardDefaults.quaternaryColors,
            ) {
                Item {
                    SettingsTextRow(
                        text = Res.string.settings_internal_backend_simulate_failure,
                        description = Res.string.settings_internal_backend_simulate_failure_desc,
                        icon = AppIcons.Lucide.Server,
                        trailingContent = {
                            Switch(
                                checked = settings.simulateFailure,
                                onCheckedChange = { value ->
                                    update(settings.copy(simulateFailure = value))
                                },
                            )
                        },
                    )
                }

                Item {
                    Column {
                        SettingsTextRow(
                            text = Res.string.settings_internal_backend_api,
                            description = Res.string.settings_internal_backend_api_desc,
                            icon = AppIcons.Lucide.Link,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
                            modifier = Modifier
                                .padding(horizontal = AppTheme.spacing.small)
                                .padding(bottom = AppTheme.spacing.small, end = AppTheme.spacing.small),
                        ) {
                            TextField(
                                value = backendApiUrl,
                                onValueChange = { backendApiUrl = it },
                                maxLines = 1,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                variant = IconButtonVariant.SecondaryElevated,
                                enabled = backendApiUrlChanged,
                                onClick = {
                                    update(settings.copy(backendApiUrl = backendApiUrl))
                                },
                                modifier = Modifier.align(Alignment.Bottom),
                            ) {
                                Icon(
                                    icon = AppIcons.Lucide.Check,
                                    contentDescription = Res.string.save.get(),
                                )
                            }
                        }
                    }
                }

                Item(isLast = true) {
                    Column(
                        modifier = Modifier.animateContentSize(),
                    ) {
                        SettingsTextRow(
                            text = Res.string.settings_internal_backend_direct_api,
                            description = Res.string.settings_internal_backend_direct_api_desc,
                            icon = AppIcons.Lucide.Server,
                            trailingContent = {
                                Switch(
                                    checked = settings.useDirectApi,
                                    onCheckedChange = { value ->
                                        update(settings.copy(useDirectApi = value))
                                    },
                                )
                            },
                        )

                        AnimatedVisibility(visible = settings.useDirectApi) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
                                modifier = Modifier
                                    .padding(horizontal = AppTheme.spacing.small)
                                    .padding(bottom = AppTheme.spacing.small, end = AppTheme.spacing.small),
                            ) {
                                TextField(
                                    value = apiKey ?: "",
                                    placeholder = {
                                        Text(
                                            Res.string.settings_internal_backend_direct_api_token_placeholder,
                                        )
                                    },
                                    onValueChange = { apiKey = it },
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    variant = IconButtonVariant.SecondaryElevated,
                                    enabled = apiKeyChanged,
                                    onClick = {
                                        update(settings.copy(apiKey = apiKey))
                                    },
                                    modifier = Modifier.align(Alignment.Bottom),
                                ) {
                                    Icon(
                                        icon = AppIcons.Lucide.Check,
                                        contentDescription = Res.string.save.get(),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Box(Modifier.padding(top = AppTheme.spacing.small)) {
                Button(
                    variant = ButtonVariant.DestructiveElevated,
                    text = Res.string.settings_internal_reset.get(),
                    onClick = {
                        val newValue = InternalSettings()
                        update(InternalSettings())
                        if (!newValue.enabled) {
                            onBack()
                        }
                    },
                    textStyle = AppTheme.typography.h4,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(AppTheme.spacing.large))
        }
    }
}

@Preview
@Composable
private fun InternalSettingsScreenPreview() {
    var settings by remember { mutableStateOf(InternalSettings(useDirectApi = true)) }
    AppPreview {
        InternalSettingsScreen(
            settings = settings,
            update = { settings = it },
        )
    }
}
