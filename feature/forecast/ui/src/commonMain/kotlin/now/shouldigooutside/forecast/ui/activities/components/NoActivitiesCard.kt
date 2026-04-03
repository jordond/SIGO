package now.shouldigooutside.forecast.ui.activities.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.activities_empty_button
import now.shouldigooutside.core.resources.activities_empty_description
import now.shouldigooutside.core.resources.activities_empty_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun NoActivitiesCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        colors = AppTheme.colors.brutal.green
            .cardColors(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            modifier = Modifier
                .padding(
                    vertical = AppTheme.spacing.large,
                    horizontal = AppTheme.spacing.standard,
                ).fillMaxWidth(),
        ) {
            Text(
                Res.string.activities_empty_title,
                style = AppTheme.typography.h2,
                textAlign = TextAlign.Center,
            )

            Text(
                Res.string.activities_empty_description,
                style = AppTheme.typography.body1,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier)

            Button(
                text = Res.string.activities_empty_button.get(),
                variant = ButtonVariant.PrimaryElevated,
                textStyle = AppTheme.typography.h2,
                onClick = onClick,
                modifier = Modifier.height(70.dp),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        Box(Modifier.padding(16.dp)) {
            NoActivitiesCard(onClick = {})
        }
    }
}
