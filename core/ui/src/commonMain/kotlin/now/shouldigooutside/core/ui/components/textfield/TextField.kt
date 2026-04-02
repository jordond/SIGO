package now.shouldigooutside.core.ui.components.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalContainerColor
import now.shouldigooutside.core.ui.components.BrutalDefaults
import now.shouldigooutside.core.ui.components.BrutalElevation
import now.shouldigooutside.core.ui.components.BrutalElevationDefaults
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.textfield.base.CommonDecorationBox
import now.shouldigooutside.core.ui.components.textfield.base.FocusedOutlineThickness
import now.shouldigooutside.core.ui.components.textfield.base.HorizontalIconPadding
import now.shouldigooutside.core.ui.components.textfield.base.LabelBottomPadding
import now.shouldigooutside.core.ui.components.textfield.base.SupportingTopPadding
import now.shouldigooutside.core.ui.components.textfield.base.TextFieldColors
import now.shouldigooutside.core.ui.components.textfield.base.TextFieldHorizontalPadding
import now.shouldigooutside.core.ui.components.textfield.base.TextFieldMinHeight
import now.shouldigooutside.core.ui.components.textfield.base.TextFieldVerticalPadding
import now.shouldigooutside.core.ui.components.textfield.base.UnfocusedOutlineThickness
import now.shouldigooutside.core.ui.components.textfield.base.containerOutline
import now.shouldigooutside.core.ui.contentColorFor
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData

@Composable
public fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTheme.typography.input,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    placeholder: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    shape: Shape = TextFieldDefaults.Shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    elevation: BrutalElevation = TextFieldDefaults.Elevation,
    cursorBrush: Brush = SolidColor(colors.cursorColor(isError).value),
) {
    val textColor =
        textStyle.color.takeOrElse {
            colors.textColor(enabled, isError, interactionSource).value
        }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides colors.selectionColors) {
        BasicTextField(
            modifier =
                modifier
                    .defaultMinSize(minHeight = TextFieldDefaults.MinHeight)
                    .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    visualTransformation = visualTransformation,
                    label = label,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    elevation = elevation,
                    colors = colors,
                    shape = shape,
                )
            },
        )
    }
}

@Composable
public fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTheme.typography.input,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    placeholder: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    shape: Shape = TextFieldDefaults.Shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    elevation: BrutalElevation = TextFieldDefaults.Elevation,
    cursorBrush: Brush = SolidColor(colors.cursorColor(isError).value),
) {
    val textColor =
        textStyle.color.takeOrElse {
            colors.textColor(enabled, isError, interactionSource).value
        }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides colors.selectionColors) {
        BasicTextField(
            modifier =
                modifier
                    .defaultMinSize(minHeight = TextFieldDefaults.MinHeight)
                    .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = value.text,
                    innerTextField = innerTextField,
                    visualTransformation = visualTransformation,
                    label = label,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    elevation = elevation,
                    colors = colors,
                    shape = shape,
                )
            },
        )
    }
}

@Immutable
public object TextFieldDefaults {
    public val MinHeight: Dp = TextFieldMinHeight
    public val Shape: Shape @Composable get() = AppTheme.shapes.small
    public val Elevation: BrutalElevation = BrutalElevationDefaults.Small

    private fun contentPadding(
        start: Dp = TextFieldHorizontalPadding,
        end: Dp = TextFieldHorizontalPadding,
        top: Dp = TextFieldVerticalPadding,
        bottom: Dp = TextFieldVerticalPadding,
    ): PaddingValues = PaddingValues(start, top, end, bottom)

    private fun labelPadding(
        start: Dp = 0.dp,
        top: Dp = 0.dp,
        end: Dp = 0.dp,
        bottom: Dp = LabelBottomPadding,
    ): PaddingValues = PaddingValues(start, top, end, bottom)

    private fun supportingTextPadding(
        start: Dp = 0.dp,
        top: Dp = SupportingTopPadding,
        end: Dp = TextFieldHorizontalPadding,
        bottom: Dp = 0.dp,
    ): PaddingValues = PaddingValues(start, top, end, bottom)

    @Composable
    private fun leadingIconPadding(
        start: Dp = HorizontalIconPadding,
        top: Dp = 0.dp,
        end: Dp = 0.dp,
        bottom: Dp = 0.dp,
    ): PaddingValues = PaddingValues(start, top, end, bottom)

    @Composable
    private fun trailingIconPadding(
        start: Dp = 0.dp,
        top: Dp = 0.dp,
        end: Dp = HorizontalIconPadding,
        bottom: Dp = 0.dp,
    ): PaddingValues = PaddingValues(start, top, end, bottom)

    @Composable
    public fun containerBorderThickness(interactionSource: InteractionSource): Dp {
        val focused by interactionSource.collectIsFocusedAsState()

        return if (focused) FocusedOutlineThickness else UnfocusedOutlineThickness
    }

    @Composable
    public fun DecorationBox(
        value: String,
        innerTextField: @Composable () -> Unit,
        enabled: Boolean,
        visualTransformation: VisualTransformation,
        interactionSource: InteractionSource,
        isError: Boolean = false,
        label: @Composable (() -> Unit)? = null,
        placeholder: @Composable (() -> Unit)? = null,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        prefix: @Composable (() -> Unit)? = null,
        suffix: @Composable (() -> Unit)? = null,
        supportingText: @Composable (() -> Unit)? = null,
        shape: Shape = Shape,
        elevation: BrutalElevation = Elevation,
        colors: TextFieldColors = colors(),
        container: @Composable () -> Unit = {
            ContainerBox(enabled, isError, interactionSource, colors, shape)
        },
    ) {
        CommonDecorationBox(
            value = value,
            innerTextField = innerTextField,
            visualTransformation = visualTransformation,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource,
            colors = colors,
            contentPadding = contentPadding(),
            labelPadding = labelPadding(),
            supportingTextPadding = supportingTextPadding(),
            leadingIconPadding = leadingIconPadding(),
            trailingIconPadding = trailingIconPadding(),
            container = container,
            shape = shape,
            elevation = elevation,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    @Composable
    public fun ContainerBox(
        enabled: Boolean,
        isError: Boolean,
        interactionSource: InteractionSource,
        colors: TextFieldColors,
        shape: Shape = Shape,
        borderThickness: Dp = containerBorderThickness(interactionSource),
    ) {
        Box(
            modifier = Modifier
                .background(colors.containerColor(enabled, isError, interactionSource).value, shape)
                .containerOutline(
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    borderThickness = borderThickness,
                    shape = shape,
                ),
        )
    }

    @Composable
    public fun colors(
        focusedTextColor: Color = AppTheme.colors.text,
        unfocusedTextColor: Color = AppTheme.colors.text,
        disabledTextColor: Color = AppTheme.colors.onDisabled,
        errorTextColor: Color = AppTheme.colors.text,
        focusedContainerColor: Color = AppTheme.colors.secondary,
        unfocusedContainerColor: Color = LocalContainerColor.current,
        disabledContainerColor: Color = LocalContainerColor.current,
        errorContainerColor: Color = LocalContainerColor.current,
        cursorColor: Color = AppTheme.colors.primary,
        errorCursorColor: Color = AppTheme.colors.error,
        textSelectionColors: TextSelectionColors = LocalTextSelectionColors.current,
        focusedOutlineColor: Color = BrutalDefaults.Color,
        unfocusedOutlineColor: Color = BrutalDefaults.Color,
        disabledOutlineColor: Color = AppTheme.colors.disabled,
        errorOutlineColor: Color = AppTheme.colors.error,
        focusedLeadingIconColor: Color = contentColorFor(focusedContainerColor),
        unfocusedLeadingIconColor: Color = contentColorFor(unfocusedContainerColor),
        disabledLeadingIconColor: Color = AppTheme.colors.onDisabled,
        errorLeadingIconColor: Color = contentColorFor(errorContainerColor),
        focusedTrailingIconColor: Color = contentColorFor(focusedContainerColor),
        unfocusedTrailingIconColor: Color = contentColorFor(unfocusedContainerColor),
        disabledTrailingIconColor: Color = AppTheme.colors.onDisabled,
        errorTrailingIconColor: Color = AppTheme.colors.error,
        focusedLabelColor: Color = AppTheme.colors.contentColorFor(LocalContainerColor.current),
        unfocusedLabelColor: Color = AppTheme.colors.contentColorFor(LocalContainerColor.current),
        disabledLabelColor: Color = AppTheme.colors.textDisabled,
        errorLabelColor: Color = AppTheme.colors.error,
        focusedPlaceholderColor: Color = AppTheme.colors.textSecondary,
        unfocusedPlaceholderColor: Color = AppTheme.colors.textSecondary,
        disabledPlaceholderColor: Color = AppTheme.colors.textDisabled,
        errorPlaceholderColor: Color = AppTheme.colors.textSecondary,
        focusedSupportingTextColor: Color = contentColorFor(LocalContainerColor.current),
        unfocusedSupportingTextColor: Color = contentColorFor(LocalContainerColor.current),
        disabledSupportingTextColor: Color = AppTheme.colors.textDisabled,
        errorSupportingTextColor: Color = AppTheme.colors.error,
        focusedPrefixColor: Color = contentColorFor(LocalContainerColor.current),
        unfocusedPrefixColor: Color = contentColorFor(LocalContainerColor.current),
        disabledPrefixColor: Color = AppTheme.colors.onDisabled,
        errorPrefixColor: Color = contentColorFor(LocalContainerColor.current),
        focusedSuffixColor: Color = contentColorFor(LocalContainerColor.current),
        unfocusedSuffixColor: Color = contentColorFor(LocalContainerColor.current),
        disabledSuffixColor: Color = AppTheme.colors.onDisabled,
        errorSuffixColor: Color = AppTheme.colors.error,
    ): TextFieldColors =
        TextFieldColors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            disabledTextColor = disabledTextColor,
            errorTextColor = errorTextColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledContainerColor = disabledContainerColor,
            errorContainerColor = errorContainerColor,
            cursorColor = cursorColor,
            errorCursorColor = errorCursorColor,
            textSelectionColors = textSelectionColors,
            focusedOutlineColor = focusedOutlineColor,
            unfocusedOutlineColor = unfocusedOutlineColor,
            disabledOutlineColor = disabledOutlineColor,
            errorOutlineColor = errorOutlineColor,
            focusedLeadingIconColor = focusedLeadingIconColor,
            unfocusedLeadingIconColor = unfocusedLeadingIconColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            errorLeadingIconColor = errorLeadingIconColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            errorTrailingIconColor = errorTrailingIconColor,
            focusedLabelColor = focusedLabelColor,
            unfocusedLabelColor = unfocusedLabelColor,
            disabledLabelColor = disabledLabelColor,
            errorLabelColor = errorLabelColor,
            focusedPlaceholderColor = focusedPlaceholderColor,
            unfocusedPlaceholderColor = unfocusedPlaceholderColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
            errorPlaceholderColor = errorPlaceholderColor,
            focusedSupportingTextColor = focusedSupportingTextColor,
            unfocusedSupportingTextColor = unfocusedSupportingTextColor,
            disabledSupportingTextColor = disabledSupportingTextColor,
            errorSupportingTextColor = errorSupportingTextColor,
            focusedPrefixColor = focusedPrefixColor,
            unfocusedPrefixColor = unfocusedPrefixColor,
            disabledPrefixColor = disabledPrefixColor,
            errorPrefixColor = errorPrefixColor,
            focusedSuffixColor = focusedSuffixColor,
            unfocusedSuffixColor = unfocusedSuffixColor,
            disabledSuffixColor = disabledSuffixColor,
            errorSuffixColor = errorSuffixColor,
        )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun TextFieldPreview() {
    AppPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            TextField(
                value = "Basic input",
                onValueChange = {},
            )
            TextField(
                value = "With label",
                onValueChange = {},
                label = { Text(text = "Label", style = AppTheme.typography.label1) },
            )
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text(text = "Placeholder", style = AppTheme.typography.body1) },
            )
            TextField(
                value = "Error state",
                onValueChange = {},
                isError = true,
                supportingText = { Text(text = "Error message", style = AppTheme.typography.body2) },
            )
            TextField(
                value = "With icons",
                onValueChange = {},
                leadingIcon = { Icon(PreviewData.Icon) },
                trailingIcon = { Icon(PreviewData.Icon) },
            )
            TextField(
                value = "With supporting text",
                onValueChange = {},
                supportingText = {
                    Text(
                        text = "Supporting text",
                        style = AppTheme.typography.body2,
                    )
                },
            )
            CompositionLocalProvider(
                LocalContainerColor provides Color.White,
            ) {
                TextField(
                    value = "With prefix and suffix",
                    onValueChange = {},
                    prefix = { Text(text = "$ ", style = AppTheme.typography.button) },
                    suffix = { Text(text = " USD", style = AppTheme.typography.button) },
                )
            }
            TextField(
                value = "Disabled state",
                onValueChange = {},
                enabled = false,
            )
        }
    }
}
