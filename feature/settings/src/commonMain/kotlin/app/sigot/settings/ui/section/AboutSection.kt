package app.sigot.settings.ui.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.Share
import app.sigot.core.ui.icons.lucide.ExternalLink
import app.sigot.core.ui.icons.lucide.FolderLock
import app.sigot.core.ui.icons.lucide.Globe
import app.sigot.core.ui.icons.lucide.Handshake
import app.sigot.core.ui.icons.lucide.Heart
import app.sigot.core.ui.preview.AppPreview
import app.sigot.settings.ui.SettingsAction
import app.sigot.settings.ui.components.SettingsCard
import app.sigot.settings.ui.components.SettingsTextRow
import app.sigot.settings.ui.components.TrailingBorderedIcon
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.open
import now.shouldigooutside.core.resources.settings_about_privacy
import now.shouldigooutside.core.resources.settings_about_rate
import now.shouldigooutside.core.resources.settings_about_share
import now.shouldigooutside.core.resources.settings_about_terms
import now.shouldigooutside.core.resources.settings_about_title
import now.shouldigooutside.core.resources.settings_about_website

private fun trailingContent(color: Color) =
    @Composable {
        TrailingBorderedIcon(
            icon = AppIcons.Lucide.ExternalLink,
            contentDescription = Res.string.open,
            color = color,
        )
    }

@Composable
internal fun AboutSection(
    dispatcher: Dispatcher<SettingsAction>,
    modifier: Modifier = Modifier,
    primary: Color = AppTheme.colors.tertiary,
    secondary: Color = AppTheme.colors.secondary,
) {
    SettingsCard(
        text = Res.string.settings_about_title,
        colors = CardDefaults.fromColor(primary),
    ) {
        Item {
            SettingsTextRow(
                text = Res.string.settings_about_share,
                onClick = dispatcher.rememberRelay(SettingsAction.ShareApp),
                icon = AppIcons.Share,
                trailingContent = trailingContent(secondary),
            )
        }

        Item {
            SettingsTextRow(
                text = Res.string.settings_about_rate,
                onClick = dispatcher.rememberRelay(SettingsAction.RateApp),
                icon = AppIcons.Lucide.Heart,
                trailingContent = trailingContent(secondary),
            )
        }

        Item {
            SettingsTextRow(
                text = Res.string.settings_about_website,
                onClick = dispatcher.rememberRelay(SettingsAction.OpenWebsite),
                icon = AppIcons.Lucide.Globe,
                trailingContent = trailingContent(secondary),
            )
        }

        Item {
            SettingsTextRow(
                text = Res.string.settings_about_privacy,
                onClick = dispatcher.rememberRelay(SettingsAction.OpenPrivacyPolicy),
                icon = AppIcons.Lucide.FolderLock,
                trailingContent = trailingContent(secondary),
            )
        }

        SettingsTextRow(
            text = Res.string.settings_about_terms,
            onClick = dispatcher.rememberRelay(SettingsAction.OpenTermsAndConditions),
            icon = AppIcons.Lucide.Handshake,
            trailingContent = trailingContent(secondary),
        )
    }
}

@Preview
@Composable
private fun AboutSectionPreview() {
    AppPreview {
        Column(Modifier.padding(16.dp)) {
            AboutSection(dispatcher = rememberDispatcher { })
        }
    }
}
