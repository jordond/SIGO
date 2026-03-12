package app.sigot.forecast.ui.section.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.location.Location
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.icons.lucide.Lucide
import app.sigot.core.ui.icons.lucide.MapPin
import app.sigot.core.ui.preview.AppPreview

@Composable
internal fun LocationResultCard(
    location: Location,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon = Lucide.MapPin,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    style = AppTheme.typography.body1,
                )
                Text(
                    text = "${location.roundedLatitude}, ${location.roundedLongitude}",
                    style = AppTheme.typography.label1,
                    color = AppTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Preview
@Composable
private fun LocationResultCardPreview() {
    AppPreview {
        LocationResultCard(
            location = Location(40.7128, -74.0060, "New York"),
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}
