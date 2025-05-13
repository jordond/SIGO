package app.sigot.core.ui.foundation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

internal val HorizontalSemanticsBoundsPadding: Dp = 10.dp

internal val IncreaseHorizontalSemanticsBounds: Modifier =
    Modifier
        .layout { measurable, constraints ->
            val paddingPx = HorizontalSemanticsBoundsPadding.roundToPx()
            // We need to add horizontal padding to the semantics bounds in order to meet
            // screenreader green box minimum size, but we also want to
            // preserve a visual appearance and layout size below that minimum
            // in order to maintain backwards compatibility. This custom
            // layout effectively implements "negative padding".
            val newConstraint = constraints.offset(paddingPx * 2, 0)
            val placeable = measurable.measure(newConstraint)

            // But when actually placing the placeable, create the layout without additional
            // space. Place the placeable where it would've been without any extra padding.
            val height = placeable.height
            val width = placeable.width - paddingPx * 2
            layout(width, height) { placeable.place(-paddingPx, 0) }
        }.semantics(mergeDescendants = true) {}
        .padding(horizontal = HorizontalSemanticsBoundsPadding)
