package app.sigot.onboarding.ui.location.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.BrutalColors
import app.sigot.core.ui.components.BrutalCircle
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.ktx.get
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.permission_status

internal class LocationCardScope(
    delegate: ColumnScope,
) : ColumnScope by delegate

@Composable
internal fun LocationCard(
    colors: BrutalColors,
    modifier: Modifier = Modifier,
    content: @Composable LocationCardScope.() -> Unit,
) {
    val containerColor by animateColorAsState(colors.container)
    val contentColor by animateColorAsState(colors.contentColorFor(colors.container))
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
        ) {
            val scope = remember(this) { LocationCardScope(this) }
            content(scope)
        }
    }
}

@Suppress("UnusedReceiverParameter")
@Composable
internal fun LocationCardScope.Header(
    icon: ImageVector,
    text: String,
    indicatorColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Crossfade(icon) { target ->
            Icon(
                icon = target,
                contentDescription = Res.string.permission_status.get(),
            )
        }

        Spacer(Modifier.width(8.dp))

        Crossfade(
            targetState = text,
            modifier = Modifier.weight(1f),
        ) { target ->
            Text(
                text = target,
                style = AppTheme.typography.h3,
            )
        }

        val animatedColor by animateColorAsState(
            targetValue = indicatorColor,
            label = "brutalCircleColor",
        )
        BrutalCircle(
            color = animatedColor,
            size = 32.dp,
        )
    }
}
