package app.sigot.core.ui.components.snackbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import app.sigot.core.resources.Res
import app.sigot.core.resources.dismiss
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.LocalContentColor
import app.sigot.core.ui.LocalTextStyle
import app.sigot.core.ui.components.BrutalContainer
import app.sigot.core.ui.components.BrutalElevationDefaults
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.Surface
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.ContainerElevation
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.ContainerMaxWidth
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.HeightToFirstLine
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.HorizontalSpacing
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.HorizontalSpacingButtonSide
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.SingleLineContainerHeight
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.SnackbarVerticalPadding
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.TextEndExtraSpacing
import app.sigot.core.ui.components.snackbar.SnackbarDefaults.TwoLinesContainerHeight
import app.sigot.core.ui.contentColorFor
import app.sigot.core.ui.ktx.get
import kotlin.math.max
import kotlin.math.min

@Composable
public fun Snackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = snackbarData.visuals.type.containerColor(),
    contentColor: Color = contentColorFor(containerColor),
    actionColor: Color = SnackbarDefaults.actionColor,
    actionContentColor: Color = contentColorFor(actionColor),
    actionVariant: ButtonVariant = SnackbarDefaults.ActionButtonVariant,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
    dismissActionVariant: ButtonVariant = SnackbarDefaults.DismissButtonVariant,
) {
    val actionLabel = snackbarData.visuals.actionLabel
    val actionComposable: (@Composable () -> Unit)? =
        if (actionLabel != null) {
            @Composable {
                CompositionLocalProvider(LocalContentColor provides actionColor) {
                    SnackbarDefaults.ActionButton(
                        text = actionLabel,
                        variant = actionVariant,
                        onClick = { snackbarData.performAction() },
                    )
                }
            }
        } else {
            null
        }
    val dismissActionComposable: (@Composable () -> Unit)? =
        if (snackbarData.visuals.withDismissAction) {
            @Composable {
                SnackbarDefaults.ActionButton(
                    text = Res.string.dismiss.get(),
                    variant = dismissActionVariant,
                    onClick = { snackbarData.dismiss() },
                )
            }
        } else {
            null
        }
    Snackbar(
        modifier = modifier,
        action = actionComposable,
        dismissAction = dismissActionComposable,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor,
        content = { Text(snackbarData.visuals.message, style = AppTheme.typography.body2) },
    )
}

@Composable
public fun Snackbar(
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    dismissAction: @Composable (() -> Unit)? = null,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
    content: @Composable () -> Unit,
) {
    BrutalContainer(
        shape = shape,
        elevation = ContainerElevation,
        border = true,
        modifier = modifier.padding(horizontal = 8.dp),
    ) {
        Surface(
            modifier = Modifier,
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            shadowElevation = 0.dp,
        ) {
            val textStyle = AppTheme.typography.body1
            val actionTextStyle = AppTheme.typography.h4
            CompositionLocalProvider(
                LocalContainerColor provides containerColor,
                LocalTextStyle provides textStyle,
            ) {
                SnackbarLayout(
                    text = content,
                    action = action,
                    dismissAction = dismissAction,
                    actionTextStyle = actionTextStyle,
                    actionTextColor = actionContentColor,
                    dismissActionColor = dismissActionContentColor,
                )
            }
        }
    }
}

@Composable
private fun SnackbarType.containerColor(): Color =
    when (this) {
        SnackbarType.Primary -> AppTheme.colors.primary
        SnackbarType.Secondary -> AppTheme.colors.secondary
        SnackbarType.Tertiary -> AppTheme.colors.tertiary
        SnackbarType.Error -> AppTheme.colors.error
    }

@Composable
private fun SnackbarLayout(
    text: @Composable () -> Unit,
    action: @Composable (() -> Unit)?,
    dismissAction: @Composable (() -> Unit)?,
    actionTextStyle: TextStyle,
    actionTextColor: Color,
    dismissActionColor: Color,
) {
    val textTag = "text"
    val actionTag = "action"
    val dismissActionTag = "dismissAction"

    Layout(
        {
            Box(
                Modifier
                    .layoutId(textTag)
                    .padding(vertical = SnackbarVerticalPadding),
            ) { text() }
            if (action != null) {
                Box(Modifier.layoutId(actionTag)) {
                    CompositionLocalProvider(
                        LocalContentColor provides actionTextColor,
                        LocalTextStyle provides actionTextStyle,
                        content = action,
                    )
                }
            }
            if (dismissAction != null) {
                Box(Modifier.layoutId(dismissActionTag)) {
                    CompositionLocalProvider(
                        LocalContentColor provides dismissActionColor,
                        content = dismissAction,
                    )
                }
            }
        },
        modifier =
            Modifier.padding(
                start = HorizontalSpacing,
                end = HorizontalSpacingButtonSide,
            ),
    ) { measurables, constraints ->
        val containerWidth = min(constraints.maxWidth, ContainerMaxWidth.roundToPx())
        val actionButtonPlaceable =
            measurables.fastFirstOrNull { it.layoutId == actionTag }?.measure(constraints)
        val dismissButtonPlaceable =
            measurables.fastFirstOrNull { it.layoutId == dismissActionTag }?.measure(constraints)
        val actionButtonWidth = actionButtonPlaceable?.width ?: 0
        val actionButtonHeight = actionButtonPlaceable?.height ?: 0
        val dismissButtonWidth = dismissButtonPlaceable?.width ?: 0
        val dismissButtonHeight = dismissButtonPlaceable?.height ?: 0
        val extraSpacingWidth = if (dismissButtonWidth == 0) TextEndExtraSpacing.roundToPx() else 0
        val textMaxWidth =
            (containerWidth - actionButtonWidth - dismissButtonWidth - extraSpacingWidth)
                .coerceAtLeast(constraints.minWidth)
        val textPlaceable =
            measurables
                .fastFirst { it.layoutId == textTag }
                .measure(constraints.copy(minHeight = 0, maxWidth = textMaxWidth))

        val firstTextBaseline = textPlaceable[FirstBaseline]
        val lastTextBaseline = textPlaceable[LastBaseline]
        val hasText =
            firstTextBaseline != AlignmentLine.Unspecified &&
                lastTextBaseline != AlignmentLine.Unspecified
        val isOneLine = firstTextBaseline == lastTextBaseline || !hasText
        val dismissButtonPlaceX = containerWidth - dismissButtonWidth - 2.dp.toPx().toInt()
        val actionButtonPlaceX = if (dismissButtonPlaceable != null) {
            dismissButtonPlaceX - actionButtonWidth - 4.dp.toPx().toInt()
        } else {
            containerWidth - actionButtonWidth
        }

        val textPlaceY: Int
        val containerHeight: Int
        val actionButtonPlaceY: Int
        if (isOneLine) {
            val minContainerHeight = SingleLineContainerHeight.roundToPx()
            val contentHeight = max(actionButtonHeight, dismissButtonHeight)
            containerHeight = max(minContainerHeight, contentHeight)
            textPlaceY = (containerHeight - textPlaceable.height) / 2
            actionButtonPlaceY =
                if (actionButtonPlaceable != null) {
                    actionButtonPlaceable[FirstBaseline].let {
                        if (it != AlignmentLine.Unspecified) {
                            textPlaceY + firstTextBaseline - it
                        } else {
                            0
                        }
                    }
                } else {
                    0
                }
        } else {
            val baselineOffset = HeightToFirstLine.roundToPx()
            textPlaceY = baselineOffset - firstTextBaseline
            val minContainerHeight = TwoLinesContainerHeight.roundToPx()
            val contentHeight = textPlaceY + textPlaceable.height
            containerHeight = max(minContainerHeight, contentHeight)
            actionButtonPlaceY =
                if (actionButtonPlaceable != null) {
                    (containerHeight - actionButtonPlaceable.height) / 2
                } else {
                    0
                }
        }
        val dismissButtonPlaceY =
            if (dismissButtonPlaceable != null) {
                (containerHeight - dismissButtonPlaceable.height) / 2
            } else {
                0
            }

        layout(containerWidth, containerHeight) {
            textPlaceable.placeRelative(0, textPlaceY)
            dismissButtonPlaceable?.placeRelative(dismissButtonPlaceX, dismissButtonPlaceY)
            actionButtonPlaceable?.placeRelative(actionButtonPlaceX, actionButtonPlaceY)
        }
    }
}

public object SnackbarDefaults {
    public val ContainerMaxWidth: Dp = 600.dp
    public val SingleLineContainerHeight: Dp = 60.dp
    public val TwoLinesContainerHeight: Dp = 70.dp
    public val HeightToFirstLine: Dp = 30.dp
    public val HorizontalSpacing: Dp = 16.dp
    public val HorizontalSpacingButtonSide: Dp = 8.dp
    public val SnackbarVerticalPadding: Dp = 6.dp
    public val TextEndExtraSpacing: Dp = 8.dp
    public val ContainerElevation: Dp = BrutalElevationDefaults.Medium.default
    public val ActionButtonVariant: ButtonVariant = ButtonVariant.Secondary
    public val DismissButtonVariant: ButtonVariant = ButtonVariant.Outlined
    private val ContainerShape @Composable get() = AppTheme.shapes.small

    public val shape: Shape
        @Composable get() = ContainerShape

    public val color: Color
        @Composable get() = AppTheme.colors.primary

    public val contentColor: Color
        @Composable get() = AppTheme.colors.onPrimary

    public val actionColor: Color
        @Composable get() = AppTheme.colors.onPrimary

    public val actionContentColor: Color
        @Composable get() = AppTheme.colors.onPrimary

    public val dismissActionContentColor: Color
        @Composable get() = AppTheme.colors.onPrimary

    @Composable
    public fun ActionButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        variant: ButtonVariant = ButtonVariant.Secondary,
        contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Button(
            modifier = modifier,
            variant = variant,
            onClick = onClick,
            shape = AppTheme.shapes.small,
            contentPadding = contentPadding,
            minHeight = Dp.Unspecified,
        ) {
            Text(text = text, style = AppTheme.typography.button)
        }
    }
}

@Composable
private fun SnackbarPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        Snackbar(
            content = { Text("This is a snackbar", style = AppTheme.typography.body2) },
        )

        Snackbar(
            content = { Text("With Action", style = AppTheme.typography.body2) },
            action = { SnackbarDefaults.ActionButton("Action", onClick = {}) },
        )

        Snackbar(
            content = { Text("With Dismiss", style = AppTheme.typography.body2) },
            dismissAction = { SnackbarDefaults.ActionButton("Dismiss", onClick = {}) },
        )

        Snackbar(
            content = { Text("With Both", style = AppTheme.typography.body2) },
            action = {
                SnackbarDefaults.ActionButton(
                    text = "Action",
                    variant = SnackbarDefaults.ActionButtonVariant,
                    onClick = {},
                )
            },
            dismissAction = {
                SnackbarDefaults.ActionButton(
                    text = "Dismiss",
                    variant = SnackbarDefaults.DismissButtonVariant,
                    onClick = {},
                )
            },
        )
    }
}

@Preview
@Composable
internal fun SnackbarLightPreview() {
    AppTheme { SnackbarPreview() }
}

@Preview
@Composable
internal fun SnackbarDarkPreview() {
    AppTheme(isDarkTheme = true) { SnackbarPreview() }
}
