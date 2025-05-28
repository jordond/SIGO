package app.sigot.settings.ui.section

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import app.sigot.core.Version
import app.sigot.core.resources.Res
import app.sigot.core.resources.settings_about_version
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.ktx.clickableWithoutRipple
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun VersionSection(
    internalSettingsEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    version: Version = Version,
) {
    Text(
        text = stringResource(
            Res.string.settings_about_version,
            version.NAME,
            version.CODE,
            version.GIT_SHA,
        ),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic,
        textDecoration = if (internalSettingsEnabled) {
            TextDecoration.Underline
        } else {
            TextDecoration.None
        },
        modifier = modifier
            .fillMaxWidth()
            .clickableWithoutRipple(onClick = onClick, haptics = false),
    )
}
