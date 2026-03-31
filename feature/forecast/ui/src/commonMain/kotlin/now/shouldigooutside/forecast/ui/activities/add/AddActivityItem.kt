package now.shouldigooutside.forecast.ui.activities.add

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.cancel
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.activities.colors
import now.shouldigooutside.core.ui.activities.rememberDisplayName
import now.shouldigooutside.core.ui.activities.rememberIcon
import now.shouldigooutside.core.ui.asDisplay
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.IconButton
import now.shouldigooutside.core.ui.components.IconButtonVariant
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.brutalBorder
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.X
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun AddActivityItem(
    activity: Activity,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = activity.rememberDisplayName()
    val icon = activity.rememberIcon()
    val colors = activity.colors()

    val containerColor by animateColorAsState(
        targetValue = if (selected) colors.container else colors.bright,
        label = "containerColor",
    )

    val iconBoxColor by animateColorAsState(
        targetValue = if (selected) colors.bright else colors.container,
        label = "iconBoxColor",
    )

    ElevatedCard(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            modifier = modifier.fillMaxSize(),
        ) {
            if (selected) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 12.dp),
                ) {
                    Content(
                        title = title,
                        colors = colors,
                        icon = icon,
                        style = AppTheme.typography.h2,
                        iconBoxColor = iconBoxColor,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onClick,
                        variant = IconButtonVariant.Outlined,
                    ) {
                        Icon(
                            icon = AppIcons.Lucide.X,
                            contentDescription = Res.string.cancel.get(),
                        )
                    }
                }
            } else {
                Content(
                    title = title,
                    colors = colors,
                    icon = icon,
                    iconBoxColor = iconBoxColor,
                    style = AppTheme.typography.body1.asDisplay.copy(
                        letterSpacing = (-0.2).em,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

@Composable
private fun Content(
    title: String,
    colors: BrutalColors,
    icon: ImageVector,
    style: TextStyle,
    iconBoxColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = iconBoxColor,
                shape = AppTheme.shapes.extraSmall,
            ).brutalBorder(shape = AppTheme.shapes.extraSmall),
    ) {
        Icon(
            icon = icon,
            contentDescription = title,
            tint = colors.containerContent,
            modifier = Modifier
                .padding(12.dp)
                .size(38.dp),
        )
    }

    Text(
        text = title,
        maxLines = 1,
        autoSize = style.autoSize(),
        style = style,
    )
}

private class Params : PreviewParameterProvider<Activity> {
    override val values: Sequence<Activity> = Activity.all.minus(Activity.General).asSequence()
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview(
    @PreviewParameter(Params::class) activity: Activity,
) {
    AppPreview {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp),
        ) {
            AddActivityItem(
                activity = activity,
                selected = true,
                onClick = { },
                modifier = Modifier.size(125.dp),
            )
            AddActivityItem(
                activity = activity,
                selected = false,
                onClick = { },
                modifier = Modifier.size(125.dp),
            )
        }
    }
}
