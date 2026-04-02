package now.shouldigooutside.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalContentColor
import now.shouldigooutside.core.ui.LocalTextStyle
import now.shouldigooutside.core.ui.components.NavigationBarDefaults.NavigationBarHeight
import now.shouldigooutside.core.ui.components.NavigationBarItemDefaults.ItemAnimationDurationMillis
import now.shouldigooutside.core.ui.components.NavigationBarItemDefaults.NavigationBarItemHorizontalPadding
import now.shouldigooutside.core.ui.components.NavigationBarItemDefaults.NavigationBarItemVerticalPadding
import now.shouldigooutside.core.ui.components.bottombar.BottomBarDefaults.BottomBarHeight
import now.shouldigooutside.core.ui.components.bottombar.BottomBarLayout
import now.shouldigooutside.core.ui.components.bottombar.BottomBarScrollBehavior
import now.shouldigooutside.core.ui.contentColorFor
import now.shouldigooutside.core.ui.foundation.ProvideTextStyle
import now.shouldigooutside.core.ui.foundation.systemBarsForVisualComponents
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.CloudSun
import now.shouldigooutside.core.ui.icons.lucide.Grid2x2
import now.shouldigooutside.core.ui.icons.lucide.House
import now.shouldigooutside.core.ui.icons.lucide.Settings
import now.shouldigooutside.core.ui.ktx.conditional
import now.shouldigooutside.core.ui.preview.AppPreview
import kotlin.math.roundToInt

@Composable
public fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    scrollBehavior: BottomBarScrollBehavior? = null,
    content: @Composable RowScope.() -> Unit,
) {
    BottomBarLayout(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    ) {
        Surface(
            color = containerColor,
            contentColor = contentColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(windowInsets)
                    .height(NavigationBarHeight),
            ) {
                HorizontalDivider(thickness = 6.dp)
                Row(
                    modifier = Modifier.selectableGroup(),
                    content = content,
                )
            }
        }
    }
}

@Composable
public fun RowScope.NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
    textStyle: TextStyle = NavigationBarItemDefaults.textStyle(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val styledIcon = @Composable {
        val iconColor by colors.iconColor(selected = selected, enabled = enabled)
        val clearSemantics = label != null && (alwaysShowLabel || selected)
        Box(modifier = if (clearSemantics) Modifier.clearAndSetSemantics {} else Modifier) {
            CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
        }
    }

    val styledLabel: @Composable (() -> Unit)? =
        label?.let {
            @Composable {
                val textColor by colors.textColor(selected = selected, enabled = enabled)
                CompositionLocalProvider(LocalContentColor provides textColor) {
                    ProvideTextStyle(textStyle, content = label)
                }
            }
        }

    Box(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            ).semantics {
                role = Role.Tab
            }.weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        val animationProgress: Float by animateFloatAsState(
            targetValue = if (selected) 1f else 0f,
            animationSpec = tween(ItemAnimationDurationMillis),
        )

        val containerColor by animateColorAsState(
            targetValue = if (selected) AppTheme.colors.primary else Color.Transparent,
            animationSpec = tween(ItemAnimationDurationMillis),
        )

        val shadowColor by animateColorAsState(
            targetValue = if (selected) AppTheme.colors.outline else Color.Transparent,
            animationSpec = tween(ItemAnimationDurationMillis),
        )

        val borderColor by animateColorAsState(
            targetValue = if (selected) AppTheme.colors.outline else Color.Transparent,
            animationSpec = tween(ItemAnimationDurationMillis),
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .conditional(selected) {
                    Modifier.graphicsLayer {
                        rotationZ = 1f * animationProgress
                    }
                },
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = (4 * animationProgress).dp, y = (4 * animationProgress).dp)
                    .background(shadowColor, shape = RoundedCornerShape(12.dp)),
            )

            Box(
                modifier = Modifier
                    .background(containerColor, shape = RoundedCornerShape(12.dp))
                    .border(
                        width = BrutalDefaults.BorderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                NavigationBarItemBaselineLayout(
                    icon = styledIcon,
                    label = styledLabel,
                    alwaysShowLabel = alwaysShowLabel,
                    animationProgress = animationProgress,
                )
            }
        }
    }
}

@Composable
private fun NavigationBarItemBaselineLayout(
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    alwaysShowLabel: Boolean,
    animationProgress: Float,
) {
    Layout(
        {
            Box(Modifier.layoutId(IconLayoutIdTag)) { icon() }

            if (label != null) {
                Box(
                    Modifier
                        .layoutId(LabelLayoutIdTag)
                        .alpha(if (alwaysShowLabel) 1f else animationProgress)
                        .padding(horizontal = NavigationBarItemHorizontalPadding / 2),
                ) { label() }
            }
        },
    ) { measurables, constraints ->
        val iconPlaceable =
            measurables.first { it.layoutId == IconLayoutIdTag }.measure(constraints)

        val labelPlaceable =
            label?.let {
                measurables.first { it.layoutId == LabelLayoutIdTag }.measure(
                    constraints.copy(minHeight = 0),
                )
            }

        if (label == null) {
            placeIcon(iconPlaceable, constraints)
        } else {
            placeLabelAndIcon(
                labelPlaceable!!,
                iconPlaceable,
                constraints,
                alwaysShowLabel,
                animationProgress,
            )
        }
    }
}

private fun MeasureScope.placeIcon(
    iconPlaceable: Placeable,
    constraints: Constraints,
): MeasureResult {
    val width = constraints.maxWidth
    val height = constraints.maxHeight

    val iconX = (width - iconPlaceable.width) / 2
    val iconY = (height - iconPlaceable.height) / 2

    return layout(width, height) {
        iconPlaceable.placeRelative(iconX, iconY)
    }
}

private fun MeasureScope.placeLabelAndIcon(
    labelPlaceable: Placeable,
    iconPlaceable: Placeable,
    constraints: Constraints,
    alwaysShowLabel: Boolean,
    animationProgress: Float,
): MeasureResult {
    val height = constraints.maxHeight

    val labelY =
        height - labelPlaceable.height - NavigationBarItemVerticalPadding.roundToPx()

    val selectedIconY = NavigationBarItemVerticalPadding.roundToPx()
    val unselectedIconY =
        if (alwaysShowLabel) selectedIconY else (height - iconPlaceable.height) / 2

    val iconDistance = unselectedIconY - selectedIconY

    val offset = (iconDistance * (1 - animationProgress)).roundToInt()

    val containerWidth = constraints.maxWidth

    val labelX = (containerWidth - labelPlaceable.width) / 2
    val iconX = (containerWidth - iconPlaceable.width) / 2

    return layout(containerWidth, height) {
        if (alwaysShowLabel || animationProgress != 0f) {
            labelPlaceable.placeRelative(labelX, labelY + offset)
        }
        iconPlaceable.placeRelative(iconX, selectedIconY + offset)
    }
}

internal object NavigationBarDefaults {
    internal val NavigationBarHeight: Dp = BottomBarHeight
    val containerColor: Color @Composable get() = AppTheme.colors.surface

    val windowInsets: WindowInsets
        @Composable get() =
            WindowInsets.systemBarsForVisualComponents.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
            )
}

public object NavigationBarItemDefaults {
    internal val NavigationBarItemHorizontalPadding: Dp = 8.dp
    internal val NavigationBarItemVerticalPadding: Dp = 12.dp
    internal const val ItemAnimationDurationMillis: Int = 100

    @Composable
    public fun colors(
        selectedIconColor: Color = AppTheme.colors.onPrimary,
        selectedTextColor: Color = AppTheme.colors.onPrimary,
        unselectedIconColor: Color = AppTheme.colors.onSurface.copy(alpha = 0.8f),
        unselectedTextColor: Color = AppTheme.colors.onSurface.copy(alpha = 0.8f),
        disabledIconColor: Color = AppTheme.colors.onSurface.copy(alpha = 0.3f),
        disabledTextColor: Color = AppTheme.colors.onSurface.copy(alpha = 0.3f),
    ): NavigationBarItemColors =
        NavigationBarItemColors(
            selectedIconColor = selectedIconColor,
            selectedTextColor = selectedTextColor,
            unselectedIconColor = unselectedIconColor,
            unselectedTextColor = unselectedTextColor,
            disabledIconColor = disabledIconColor,
            disabledTextColor = disabledTextColor,
        )

    @Composable
    public fun textStyle(): TextStyle = AppTheme.typography.label3
}

@ConsistentCopyVisibility
@Stable
public data class NavigationBarItemColors internal constructor(
    private val selectedIconColor: Color,
    private val selectedTextColor: Color,
    private val unselectedIconColor: Color,
    private val unselectedTextColor: Color,
    private val disabledIconColor: Color,
    private val disabledTextColor: Color,
) {
    @Composable
    internal fun iconColor(
        selected: Boolean,
        enabled: Boolean,
    ): State<Color> {
        val targetValue =
            when {
                !enabled -> disabledIconColor
                selected -> selectedIconColor
                else -> unselectedIconColor
            }
        return animateColorAsState(
            targetValue = targetValue,
            animationSpec = tween(ItemAnimationDurationMillis),
            label = "icon-color",
        )
    }

    @Composable
    internal fun textColor(
        selected: Boolean,
        enabled: Boolean,
    ): State<Color> {
        val targetValue =
            when {
                !enabled -> disabledTextColor
                selected -> selectedTextColor
                else -> unselectedTextColor
            }
        return animateColorAsState(
            targetValue = targetValue,
            animationSpec = tween(ItemAnimationDurationMillis),
            label = "text-color",
        )
    }
}

private const val IconLayoutIdTag: String = "icon"
private const val LabelLayoutIdTag: String = "label"

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        var selectedItem by remember { mutableStateOf("Activities") }
        val items = mapOf(
            "Home" to AppIcons.Lucide.House,
            "Forecast" to AppIcons.Lucide.CloudSun,
            "Activities" to AppIcons.Lucide.Grid2x2,
            "Settings" to AppIcons.Lucide.Settings,
        )

        Column(modifier = Modifier.padding(16.dp).background(AppTheme.colors.background)) {
            NavigationBar {
                items.forEach { (key, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = key) },
                        label = {
                            Text(
                                text = key.uppercase(),
                                maxLines = 1,
                                autoSize = LocalTextStyle.current.autoSize(),
                            )
                        },
                        selected = selectedItem == key,
                        onClick = { selectedItem = key },
                    )
                }
            }
        }
    }
}
