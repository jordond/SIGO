package app.sigot.settings.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.sigot.core.resources.Res
import app.sigot.core.resources.back
import app.sigot.core.resources.close
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.ArrowLeft
import app.sigot.core.ui.icons.lucide.X
import app.sigot.core.ui.ktx.get
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsTopBar(
    text: StringResource,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isBack: Boolean = false,
) {
    LargeTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = text.get(),
                style = AppTheme.typography.h1,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                variant = IconButtonVariant.Outlined,
            ) {
                val icon = remember(isBack) {
                    if (isBack) AppIcons.Lucide.ArrowLeft else AppIcons.Lucide.X
                }
                val description = remember {
                    if (isBack) Res.string.back else Res.string.close
                }
                Icon(
                    icon = icon,
                    contentDescription = description.get(),
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = AppTheme.colors.surface,
            navigationIconContentColor = AppTheme.colors.onSurface,
            titleContentColor = AppTheme.colors.onSurface,
        ),
    )
}
