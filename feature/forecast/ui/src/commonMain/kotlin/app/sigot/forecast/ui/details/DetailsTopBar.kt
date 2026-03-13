package app.sigot.forecast.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.location.Location
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.LocalContentColor
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.ArrowBigDown
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.back
import now.shouldigooutside.core.resources.forecast_details_title

@Composable
internal fun DetailsTopBar(
    location: Location,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = Res.string.forecast_details_title.get(),
                    style = AppTheme.typography.h1,
                )
                if (!location.isDefaultName) {
                    Text(
                        text = location.name,
                        style = AppTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                variant = IconButtonVariant.Outlined,
            ) {
                Icon(
                    icon = AppIcons.Lucide.ArrowBigDown,
                    contentDescription = Res.string.back.get(),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalContainerColor.current,
            navigationIconContentColor = LocalContentColor.current,
            titleContentColor = LocalContentColor.current,
        ),
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun DetailsTopBarPreview() {
    AppPreview {
        DetailsTopBar(
            location = Location(43.6532, -79.3832, "London"),
            onBack = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun DetailsTopBarEmptyLocationPreview() {
    AppPreview {
        DetailsTopBar(
            location = Location(43.6532, -79.3832),
            onBack = {},
        )
    }
}
