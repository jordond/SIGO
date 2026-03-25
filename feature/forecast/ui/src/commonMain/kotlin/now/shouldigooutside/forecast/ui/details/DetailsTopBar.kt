package now.shouldigooutside.forecast.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_details_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalContainerColor
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.topbar.TopBar
import now.shouldigooutside.core.ui.components.topbar.TopBarDefaults
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun DetailsTopBar(
    location: Location,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopBar(
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
            BackButton(onClick = onBack)
        },
        colors = TopBarDefaults.topBarColors(
            containerColor = LocalContainerColor.current,
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
