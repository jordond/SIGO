package now.shouldigooutside.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.cancel
import now.shouldigooutside.core.resources.okay
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.BrutalColor
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.LocalContentColor
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.components.AlertDialogDefaults.ButtonsCrossAxisSpacing
import now.shouldigooutside.core.ui.components.AlertDialogDefaults.ButtonsMainAxisSpacing
import now.shouldigooutside.core.ui.components.AlertDialogDefaults.DialogMaxWidth
import now.shouldigooutside.core.ui.components.AlertDialogDefaults.DialogMinWidth
import now.shouldigooutside.core.ui.components.AlertDialogDefaults.IconPadding
import now.shouldigooutside.core.ui.components.AlertDialogDefaults.TextPadding
import now.shouldigooutside.core.ui.components.AlertDialogDefaults.TitlePadding
import now.shouldigooutside.core.ui.foundation.ProvideContentColorTextStyle
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import kotlin.math.max

@Composable
public fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    confirmButtonText: String = Res.string.okay.get(),
    dismissButtonText: String? = Res.string.cancel.get(),
    icon: (@Composable () -> Unit)? = null,
    colors: BrutalColors = AppTheme.colors.brutal.red,
    iconContentColor: Color = AppTheme.colors.onSurface,
    titleContentColor: Color = AppTheme.colors.onSurface,
    textContentColor: Color = AppTheme.colors.onSurface,
    properties: DialogProperties = DialogProperties(),
    content: @Composable (() -> Unit)? = null,
) {
    AlertDialogComponent(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                variant = ButtonVariant.PrimaryElevated,
                text = confirmButtonText,
                onClick = onConfirmClick,
                modifier = Modifier.widthIn(min = 100.dp),
            )
        },
        modifier = modifier,
        dismissButton =
            if (dismissButtonText == null) {
                null
            } else {
                {
                    Button(
                        variant = ButtonVariant.Elevated,
                        text = dismissButtonText,
                        onClick = onDismissRequest,
                    )
                }
            },
        icon = icon,
        title = { Text(text = title) },
        text = { Text(text = text) },
        colors = colors,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        properties = properties,
        content = content,
    )
}

@Composable
public fun BasicAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    colors: BrutalColors = AppTheme.colors.brutal.red,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        val dialogPaneDescription = "dialog"
        Box(
            modifier =
                modifier
                    .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
                    .padding(BrutalElevationDefaults.Medium.default)
                    .then(Modifier.semantics { paneTitle = dialogPaneDescription }),
        ) {
            RetroBox(
                colors = colors,
                maxWidth = DialogMaxWidth,
            ) {
                content()
            }
        }
    }
}

@Composable
private fun AlertDialogComponent(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier,
    dismissButton: @Composable (() -> Unit)?,
    icon: @Composable (() -> Unit)?,
    title: @Composable (() -> Unit)?,
    text: @Composable (() -> Unit)?,
    colors: BrutalColors,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
    properties: DialogProperties,
    content: @Composable (() -> Unit)? = null,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = properties,
        colors = colors,
    ) {
        if (content != null) {
            content()
        } else {
            AlertDialogContent(
                buttons = {
                    AlertDialogFlowRow(
                        mainAxisSpacing = ButtonsMainAxisSpacing,
                        crossAxisSpacing = ButtonsCrossAxisSpacing,
                    ) {
                        dismissButton?.invoke()
                        confirmButton()
                    }
                },
                icon = icon,
                title = title,
                text = text,
                buttonContentColor = iconContentColor,
                iconContentColor = iconContentColor,
                titleContentColor = titleContentColor,
                textContentColor = textContentColor,
            )
        }
    }
}

@Composable
internal fun AlertDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)?,
    title: (@Composable () -> Unit)?,
    text: @Composable (() -> Unit)?,
    buttonContentColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
) {
    Column(modifier = modifier) {
        icon?.let {
            CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                Box(
                    Modifier
                        .padding(IconPadding)
                        .align(Alignment.CenterHorizontally),
                ) {
                    icon()
                }
            }
        }
        title?.let {
            ProvideContentColorTextStyle(
                contentColor = titleContentColor,
                textStyle = AppTheme.typography.h3,
            ) {
                Box(
                    Modifier
                        .padding(TitlePadding)
                        .align(
                            if (icon == null) {
                                Alignment.Start
                            } else {
                                Alignment.CenterHorizontally
                            },
                        ),
                ) {
                    title()
                }
            }
        }
        text?.let {
            val textStyle = AppTheme.typography.body1
            ProvideContentColorTextStyle(
                contentColor = textContentColor,
                textStyle = textStyle,
            ) {
                Box(
                    Modifier
                        .weight(weight = 1f, fill = false)
                        .padding(TextPadding)
                        .align(Alignment.Start),
                ) {
                    text()
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.End)) {
            val textStyle = AppTheme.typography.body2
            ProvideContentColorTextStyle(
                contentColor = buttonContentColor,
                textStyle = textStyle,
                content = buttons,
            )
        }
    }
}

@Composable
internal fun AlertDialogFlowRow(
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit,
) {
    Layout(content) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Return whether the placeable can be added to the current sequence.
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() ||
                currentMainAxisSize + mainAxisSpacing.roundToPx() + placeable.width <= constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                crossAxisSpace += crossAxisSpacing.roundToPx()
            }
            // Ensures that confirming actions appear above dismissive actions.
            @Suppress("ListIterator")
            sequences.add(0, currentSequence.toList())
            crossAxisSizes += currentCrossAxisSize
            crossAxisPositions += crossAxisSpace

            crossAxisSpace += currentCrossAxisSize
            mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

            currentSequence.clear()
            currentMainAxisSize = 0
            currentCrossAxisSize = 0
        }

        measurables.fastForEach { measurable ->
            // Ask the child for its preferred size.
            val placeable = measurable.measure(constraints)

            // Start a new sequence if there is not enough space.
            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            // Add the child to the current sequence.
            if (currentSequence.isNotEmpty()) {
                currentMainAxisSize += mainAxisSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentMainAxisSize += placeable.width
            currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

        val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

        val layoutWidth = mainAxisLayoutSize

        val layoutHeight = crossAxisLayoutSize

        layout(layoutWidth, layoutHeight) {
            sequences.fastForEachIndexed { i, placeables ->
                val childrenMainAxisSizes =
                    IntArray(placeables.size) { j ->
                        placeables[j].width + if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
                    }
                val arrangement = Arrangement.End
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size)
                with(arrangement) {
                    arrange(
                        totalSize = mainAxisLayoutSize,
                        sizes = childrenMainAxisSizes,
                        layoutDirection = layoutDirection,
                        outPositions = mainAxisPositions,
                    )
                }
                placeables.fastForEachIndexed { j, placeable ->
                    placeable.place(x = mainAxisPositions[j], y = crossAxisPositions[i])
                }
            }
        }
    }
}

public object AlertDialogDefaults {
    public val DialogMinWidth: Dp = 300.dp
    public val DialogMaxWidth: Dp = 600.dp

    public val ButtonsMainAxisSpacing: Dp = 8.dp
    public val ButtonsCrossAxisSpacing: Dp = 12.dp

    public val IconPadding: PaddingValues = PaddingValues(bottom = 16.dp)
    public val TitlePadding: PaddingValues = PaddingValues(bottom = 8.dp)
    public val TextPadding: PaddingValues = PaddingValues(bottom = 24.dp)
}

private class Params : PreviewParameterProvider<BrutalColors> {
    override val values: Sequence<BrutalColors> = BrutalColor.all.asSequence()
}

@Preview
@Composable
private fun DialogPreview(
    @PreviewParameter(Params::class) colors: BrutalColors,
) {
    AppPreview {
        Column(
            modifier = Modifier.size(400.dp),
        ) {
            AlertDialog(
                colors = colors,
                onDismissRequest = { },
                onConfirmClick = { },
                title = "Simple Alert",
                text = "This is a basic alert dialog with default buttons",
                confirmButtonText = "OK",
                dismissButtonText = "Cancel",
            )
        }
    }
}

@Preview
@Composable
private fun AlertDialogPreviews() {
    AppPreview {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var showSimpleDialog by remember { mutableStateOf(false) }
            var showSingleButtonDialog by remember { mutableStateOf(false) }
            var showLongContentDialog by remember { mutableStateOf(false) }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    variant = ButtonVariant.Primary,
                    text = "Show Simple Dialog",
                    onClick = { showSimpleDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    variant = ButtonVariant.Primary,
                    text = "Show Single Button Dialog",
                    onClick = { showSingleButtonDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    variant = ButtonVariant.Primary,
                    text = "Show Long Content Dialog",
                    onClick = { showLongContentDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (showSimpleDialog) {
                AlertDialog(
                    onDismissRequest = { showSimpleDialog = false },
                    onConfirmClick = { showSimpleDialog = false },
                    title = "Simple Alert",
                    text = "This is a basic alert dialog with default buttons",
                    confirmButtonText = "OK",
                    dismissButtonText = "Cancel",
                )
            }

            if (showSingleButtonDialog) {
                AlertDialog(
                    onDismissRequest = { showSingleButtonDialog = false },
                    onConfirmClick = { showSingleButtonDialog = false },
                    title = "Information",
                    text = "This alert only has a confirmation button",
                    confirmButtonText = "Got it",
                    dismissButtonText = null, // Removes the dismiss button
                )
            }

            if (showLongContentDialog) {
                AlertDialog(
                    onDismissRequest = { showLongContentDialog = false },
                    onConfirmClick = { showLongContentDialog = false },
                    title = "Terms & Conditions",
                    text =
                        "This is a longer content example that demonstrates how the alert dialog handles " +
                            "multiple lines of text. The content will automatically adjust to show longer " +
                            "messages while maintaining readability. This is particularly useful for " +
                            "displaying terms and conditions or detailed information to users.",
                    confirmButtonText = "Accept",
                    dismissButtonText = "Decline",
                )
            }
        }
    }
}
