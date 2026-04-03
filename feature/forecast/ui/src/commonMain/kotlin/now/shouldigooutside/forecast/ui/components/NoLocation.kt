package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_no_location_description
import now.shouldigooutside.core.resources.forecast_no_location_title
import now.shouldigooutside.core.resources.forecast_set_location
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.MapPin
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun NoLocation(
    onSetLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
        modifier = modifier.padding(top = AppTheme.spacing.large),
    ) {
        Icon(
            AppIcons.Lucide.MapPin,
            modifier = Modifier.size(48.dp),
        )

        Text(
            text = Res.string.forecast_no_location_title.get(),
            style = AppTheme.typography.h3,
        )

        Text(
            text = Res.string.forecast_no_location_description.get(),
            style = AppTheme.typography.body1,
        )

        Button(
            onClick = onSetLocation,
            variant = ButtonVariant.PrimaryElevated,
        ) {
            Text(
                text = Res.string.forecast_set_location.get(),
                style = AppTheme.typography.h4,
            )
        }
    }
}

@Preview
@Composable
private fun NoLocationPreview() {
    AppPreview {
        NoLocation(
            onSetLocation = {},
            modifier = Modifier.padding(32.dp),
        )
    }
}
