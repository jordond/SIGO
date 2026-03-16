package now.shouldigooutside.core.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.nomanr.composables.internal.MotionSchemeKeyTokens
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalContentColor
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.foundation.ProvideTextStyle
import now.shouldigooutside.core.ui.foundation.ripple
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Check
import now.shouldigooutside.core.ui.icons.lucide.Droplet
import now.shouldigooutside.core.ui.icons.lucide.Share
import now.shouldigooutside.core.ui.icons.lucide.Wind
import now.shouldigooutside.core.ui.preview.AppPreview
import kotlin.math.max
import kotlin.math.min

/**
 * [Material Design dropdown menu](https://m3.material.io/components/menus/overview)
 *
 * Menus display a list of choices on a temporary surface. They appear when users interact with a
 * button, action, or other control.
 *
 * ![Dropdown menu
 * image](https://developer.android.com/images/reference/androidx/compose/material3/menu.png)
 *
 * A [DropdownMenu] behaves similarly to a [Popup], and will use the position of the parent layout
 * to position itself on screen. Commonly a [DropdownMenu] will be placed in a [Box] with a sibling
 * that will be used as the 'anchor'. Note that a [DropdownMenu] by itself will not take up any
 * space in a layout, as the menu is displayed in a separate window, on top of other content.
 *
 * The [content] of a [DropdownMenu] will typically be [DropdownMenuItem]s, as well as custom
 * content. Using [DropdownMenuItem]s will result in a menu that matches the Material specification
 * for menus. Also note that the [content] is placed inside a scrollable [Column], so using a
 * [androidx.compose.foundation.lazy.LazyColumn] as the root layout inside [content] is unsupported.
 *
 * [onDismissRequest] will be called when the menu should close - for example when there is a tap
 * outside the menu, or when the back key is pressed.
 *
 * [DropdownMenu] changes its positioning depending on the available space, always trying to be
 * fully visible. Depending on layout direction, first it will try to align its start to the start
 * of its parent, then its end to the end of its parent, and then to the edge of the window.
 * Vertically, it will try to align its top to the bottom of its parent, then its bottom to top of
 * its parent, and then to the edge of the window.
 *
 * An [offset] can be provided to adjust the positioning of the menu for cases when the layout
 * bounds of its parent do not coincide with its visual bounds.
 *
 * @param expanded whether the menu is expanded or not
 * @param onDismissRequest called when the user requests to dismiss the menu, such as by tapping
 *   outside the menu's bounds
 * @param modifier [Modifier] to be applied to the menu's content
 * @param offset [DpOffset] from the original position of the menu. The offset respects the
 *   [androidx.compose.ui.unit.LayoutDirection], so the offset's x position will be added in LTR and subtracted in RTL.
 * @param scrollState a [ScrollState] to used by the menu's content for items vertical scrolling
 * @param properties [PopupProperties] for further customization of this popup's behavior
 * @param shape the shape of the menu
 * @param containerColor the container color of the menu
 * @param elevation the elevation for the shadow below the menu
 * @param border the border to draw around the container of the menu. Pass `null` for no border.
 * @param content the content of this dropdown menu, typically a [DropdownMenuItem]
 */
@Composable
public expect fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 12.dp),
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = DefaultMenuProperties,
    shape: Shape = MenuDefaults.shape,
    containerColor: Color = MenuDefaults.containerColor,
    elevation: Dp = MenuDefaults.Elevation,
    border: BorderStroke? = BrutalDefaults.Border,
    content: @Composable ColumnScope.() -> Unit,
)

/**
 * [Material Design dropdown menu](https://m3.material.io/components/menus/overview)
 *
 * Menus display a list of choices on a temporary surface. They appear when users interact with a
 * button, action, or other control.
 *
 * @param text text of the menu item
 * @param onClick called when this menu item is clicked
 * @param modifier the [Modifier] to be applied to this menu item
 * @param leadingIcon optional leading icon to be displayed at the beginning of the item's text
 * @param trailingIcon optional trailing icon to be displayed at the end of the item's text. This
 *   trailing icon slot can also accept [Text] to indicate a keyboard shortcut.
 * @param enabled controls the enabled state of this menu item. When `false`, this component will
 *   not respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param colors [MenuItemColors] that will be used to resolve the colors used for this menu item in
 *   different states. See [MenuDefaults.itemColors].
 * @param contentPadding the padding applied to the content of this menu item
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this menu item. You can use this to change the menu item's
 *   appearance or preview the menu item in different states. Note that if `null` is provided,
 *   interactions will still happen internally.
 */
@Composable
public expect fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
)

/** Contains default values used for [DropdownMenu] and [DropdownMenuItem]. */
public object MenuDefaults {
    public val properties: PopupProperties = DefaultMenuProperties

    /** The default shadow elevation for a menu. */
    public val Elevation: Dp = BrutalElevationDefaults.Medium.default

    /** The default shape for a menu. */
    public val shape: CornerBasedShape
        @Composable get() = AppTheme.shapes.small

    /** The default container color for a menu. */
    public val containerColor: Color
        @Composable get() = AppTheme.colors.brutal.yellow.low

    public val LeadingIconSize: Dp = 24.dp
    public val TrailingIconSize: Dp = 24.dp

    /**
     * Creates a [MenuItemColors] that represents the default text and icon colors used in a
     * [DropdownMenuItemContent].
     *
     * @param textColor the text color of this [DropdownMenuItemContent] when enabled
     * @param leadingIconColor the leading icon color of this [DropdownMenuItemContent] when enabled
     * @param trailingIconColor the trailing icon color of this [DropdownMenuItemContent] when enabled
     * @param disabledTextColor the text color of this [DropdownMenuItemContent] when not enabled
     * @param disabledLeadingIconColor the leading icon color of this [DropdownMenuItemContent] when not enabled
     * @param disabledTrailingIconColor the trailing icon color of this [DropdownMenuItemContent] when not enabled
     */
    @Composable
    public fun itemColors(
        textColor: Color = AppTheme.colors.onSurface,
        leadingIconColor: Color = AppTheme.colors.onSurface,
        trailingIconColor: Color = AppTheme.colors.onSurface,
        disabledTextColor: Color = AppTheme.colors.textDisabled,
        disabledLeadingIconColor: Color = AppTheme.colors.textDisabled,
        disabledTrailingIconColor: Color = AppTheme.colors.textDisabled,
    ): MenuItemColors =
        MenuItemColors(
            textColor = textColor,
            leadingIconColor = leadingIconColor,
            trailingIconColor = trailingIconColor,
            disabledTextColor = disabledTextColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
        )

    /** Default padding used for [DropdownMenuItem]. */
    public val DropdownMenuItemContentPadding: PaddingValues =
        PaddingValues(horizontal = DropdownMenuItemHorizontalPadding, vertical = 0.dp)
}

internal expect val DefaultMenuProperties: PopupProperties

/**
 * Represents the text and icon colors used in a menu item at different states.
 *
 * @param textColor the text color of this [DropdownMenuItemContent] when enabled
 * @param leadingIconColor the leading icon color of this [DropdownMenuItemContent] when enabled
 * @param trailingIconColor the trailing icon color of this [DropdownMenuItemContent] when enabled
 * @param disabledTextColor the text color of this [DropdownMenuItemContent] when not enabled
 * @param disabledLeadingIconColor the leading icon color of this [DropdownMenuItemContent] when not enabled
 * @param disabledTrailingIconColor the trailing icon color of this [DropdownMenuItemContent] when not enabled
 * @constructor create an instance with arbitrary colors. See [MenuDefaults.itemColors] for the default colors used in a [DropdownMenuItemContent].
 */
@Immutable
public class MenuItemColors(
    public val textColor: Color,
    public val leadingIconColor: Color,
    public val trailingIconColor: Color,
    public val disabledTextColor: Color,
    public val disabledLeadingIconColor: Color,
    public val disabledTrailingIconColor: Color,
) {
    /**
     * Returns a copy of this MenuItemColors, optionally overriding some of the values. This uses the Color.Unspecified to mean “use the value from the source”
     */
    public fun copy(
        textColor: Color = this.textColor,
        leadingIconColor: Color = this.leadingIconColor,
        trailingIconColor: Color = this.trailingIconColor,
        disabledTextColor: Color = this.disabledTextColor,
        disabledLeadingIconColor: Color = this.disabledLeadingIconColor,
        disabledTrailingIconColor: Color = this.disabledTrailingIconColor,
    ): MenuItemColors =
        MenuItemColors(
            textColor.takeOrElse { this.textColor },
            leadingIconColor.takeOrElse { this.leadingIconColor },
            trailingIconColor.takeOrElse { this.trailingIconColor },
            disabledTextColor.takeOrElse { this.disabledTextColor },
            disabledLeadingIconColor.takeOrElse { this.disabledLeadingIconColor },
            disabledTrailingIconColor.takeOrElse { this.disabledTrailingIconColor },
        )

    /**
     * Represents the text color for a menu item, depending on its [enabled] state.
     *
     * @param enabled whether the menu item is enabled
     */
    @Stable
    internal fun textColor(enabled: Boolean): Color = if (enabled) textColor else disabledTextColor

    /**
     * Represents the leading icon color for a menu item, depending on its [enabled] state.
     *
     * @param enabled whether the menu item is enabled
     */
    @Stable
    internal fun leadingIconColor(enabled: Boolean): Color =
        if (enabled) leadingIconColor else disabledLeadingIconColor

    /**
     * Represents the trailing icon color for a menu item, depending on its [enabled] state.
     *
     * @param enabled whether the menu item is enabled
     */
    @Stable
    internal fun trailingIconColor(enabled: Boolean): Color =
        if (enabled) trailingIconColor else disabledTrailingIconColor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is MenuItemColors) return false

        if (textColor != other.textColor) return false
        if (leadingIconColor != other.leadingIconColor) return false
        if (trailingIconColor != other.trailingIconColor) return false
        if (disabledTextColor != other.disabledTextColor) return false
        if (disabledLeadingIconColor != other.disabledLeadingIconColor) return false
        if (disabledTrailingIconColor != other.disabledTrailingIconColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = textColor.hashCode()
        result = 31 * result + leadingIconColor.hashCode()
        result = 31 * result + trailingIconColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()
        result = 31 * result + disabledLeadingIconColor.hashCode()
        result = 31 * result + disabledTrailingIconColor.hashCode()
        return result
    }
}

@Composable
internal fun DropdownMenuContent(
    modifier: Modifier,
    expandedState: MutableTransitionState<Boolean>,
    transformOriginState: MutableState<TransformOrigin>,
    scrollState: ScrollState,
    shape: Shape,
    containerColor: Color,
    elevation: Dp,
    border: BorderStroke?,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Menu open/close animation.
    @Suppress("DEPRECATION")
    val transition = updateTransition(expandedState, "DropDownMenu")
    val scaleAnimationSpec = MotionSchemeKeyTokens.DefaultSpatial.value()
    val scale by
        transition.animateFloat(transitionSpec = { scaleAnimationSpec }) { expanded ->
            if (expanded) EXPANDED_SCALE_TARGET else CLOSED_SCALE_TARGET
        }

    val isInspecting = LocalInspectionMode.current
    BrutalContainer(
        shape = shape,
        elevation = elevation,
        modifier = Modifier.graphicsLayer {
            scaleX =
                if (!isInspecting) {
                    scale
                } else if (expandedState.targetState) {
                    EXPANDED_SCALE_TARGET
                } else {
                    CLOSED_SCALE_TARGET
                }
            scaleY =
                if (!isInspecting) {
                    scale
                } else if (expandedState.targetState) {
                    EXPANDED_SCALE_TARGET
                } else {
                    CLOSED_SCALE_TARGET
                }
            transformOrigin = transformOriginState.value
        },
    ) {
        Surface(
            shape = shape,
            color = containerColor,
            shadowElevation = elevation,
            border = border,
        ) {
            Column(
                modifier =
                    modifier
                        .padding(vertical = DropdownMenuVerticalPadding)
                        .width(IntrinsicSize.Max)
                        .verticalScroll(scrollState),
                content = content,
            )
        }
    }
}

@Composable
internal fun DropdownMenuItemContent(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    enabled: Boolean,
    colors: MenuItemColors,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource?,
) {
    Row(
        modifier =
            modifier
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true),
                ).fillMaxWidth()
                // Preferred min and max width used during the intrinsic measurement.
                .sizeIn(
                    minWidth = DropdownMenuItemDefaultMinWidth,
                    maxWidth = DropdownMenuItemDefaultMaxWidth,
                    minHeight = MenuListItemContainerHeight,
                ).padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProvideTextStyle(AppTheme.typography.label1) {
            if (leadingIcon != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.leadingIconColor(enabled),
                ) {
                    Box(Modifier.defaultMinSize(minWidth = MenuDefaults.LeadingIconSize)) {
                        leadingIcon()
                    }
                }
            }
            CompositionLocalProvider(LocalContentColor provides colors.textColor(enabled)) {
                Box(
                    Modifier
                        .weight(1f)
                        .padding(
                            start =
                                if (leadingIcon != null) {
                                    DropdownMenuItemHorizontalPadding
                                } else {
                                    0.dp
                                },
                            end =
                                if (trailingIcon != null) {
                                    DropdownMenuItemHorizontalPadding
                                } else {
                                    0.dp
                                },
                        ),
                ) {
                    text()
                }
            }
            if (trailingIcon != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.trailingIconColor(enabled),
                ) {
                    Box(Modifier.defaultMinSize(minWidth = MenuDefaults.TrailingIconSize)) {
                        trailingIcon()
                    }
                }
            }
        }
    }
}

internal fun calculateTransformOrigin(
    anchorBounds: IntRect,
    menuBounds: IntRect,
): TransformOrigin {
    val pivotX =
        when {
            menuBounds.left >= anchorBounds.right -> {
                0f
            }
            menuBounds.right <= anchorBounds.left -> {
                1f
            }
            menuBounds.width == 0 -> {
                0f
            }
            else -> {
                val intersectionCenter =
                    (
                        max(anchorBounds.left, menuBounds.left) +
                            min(anchorBounds.right, menuBounds.right)
                    ) / 2
                (intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
            }
        }
    val pivotY =
        when {
            menuBounds.top >= anchorBounds.bottom -> {
                0f
            }
            menuBounds.bottom <= anchorBounds.top -> {
                1f
            }
            menuBounds.height == 0 -> {
                0f
            }
            else -> {
                val intersectionCenter =
                    (
                        max(anchorBounds.top, menuBounds.top) +
                            min(anchorBounds.bottom, menuBounds.bottom)
                    ) / 2
                (intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
            }
        }
    return TransformOrigin(pivotX, pivotY)
}

internal val MenuVerticalMargin = 48.dp
private val MenuListItemContainerHeight = 48.dp
private val DropdownMenuItemHorizontalPadding = 12.dp
internal val DropdownMenuVerticalPadding = 0.dp
private val DropdownMenuItemDefaultMinWidth = 150.dp
private val DropdownMenuItemDefaultMaxWidth = 280.dp

// Menu open/close animation.
internal const val EXPANDED_SCALE_TARGET = 1f
internal const val CLOSED_SCALE_TARGET = 0f

@Preview
@Composable
private fun DropdownMenuPreview() {
    var expanded by remember { mutableStateOf(false) }
    AppPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopEnd)
                .padding(end = 24.dp),
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert)
            }
            DropdownMenu(
                expanded = expanded,
                offset = DpOffset(0.dp, 10.dp),
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Item 1") },
                    leadingIcon = {
                        Icon(icon = AppIcons.Lucide.Droplet)
                    },
                    onClick = { expanded = false },
                )
                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Item 3") },
                    trailingIcon = {
                        Icon(icon = AppIcons.Lucide.Wind)
                    },
                    onClick = { expanded = false },
                )
                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Item 4") },
                    trailingIcon = {
                        Icon(icon = AppIcons.Lucide.Check)
                    },
                    leadingIcon = {
                        Icon(icon = AppIcons.Lucide.Share)
                    },
                    onClick = { expanded = false },
                )
                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Item 5") },
                    onClick = { expanded = false },
                )
            }
        }
    }
}

@Preview
@Composable
private fun DropdownMenuItemPreview() {
    AppPreview {
        Column {
            DropdownMenu(
                expanded = true,
                onDismissRequest = {},
            ) {
                DropdownMenuItem(
                    text = { Text("Item 1") },
                    onClick = {},
                )

                DropdownMenuItem(
                    text = { Text("Item 3") },
                    onClick = {},
                )

                DropdownMenuItem(
                    text = { Text("Item 4") },
                    onClick = {},
                )

                DropdownMenuItem(
                    text = { Text("Item 5") },
                    onClick = {},
                )
            }
        }
    }
}
