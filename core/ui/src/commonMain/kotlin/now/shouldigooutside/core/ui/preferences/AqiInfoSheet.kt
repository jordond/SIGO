package now.shouldigooutside.core.ui.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.aqi_info_description
import now.shouldigooutside.core.resources.aqi_info_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.AqiLevel
import now.shouldigooutside.core.ui.AqiLevels
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.ModalBottomSheet
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
public fun AqiInfoSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        isVisible = isVisible,
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppTheme.spacing.standard)
                .padding(bottom = AppTheme.spacing.standard),
        ) {
            Text(
                text = Res.string.aqi_info_title,
                style = AppTheme.typography.h2,
            )

            Text(
                text = Res.string.aqi_info_description,
                style = AppTheme.typography.body1,
            )

            AqiLevels.all().forEach { level ->
                AqiLevelCard(level)
            }
        }
    }
}

@Composable
private fun AqiLevelCard(
    level: AqiLevel,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = level.colors.cardColors(),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .background(level.colors.bright)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = level.title,
                    style = AppTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = level.range,
                    style = AppTheme.typography.h4,
                )
            }

            HorizontalDivider()

            Text(
                text = level.description,
                style = AppTheme.typography.body2,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

@Preview
@Composable
private fun AqiInfoSheetPreview() {
    AppPreview {
        AqiInfoSheet(
            isVisible = true,
            onDismiss = {},
        )
    }
}
