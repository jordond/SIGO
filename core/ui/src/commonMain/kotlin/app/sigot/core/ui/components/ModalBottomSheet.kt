package app.sigot.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContentColor
import app.sigot.core.ui.preview.AppPreview
import com.nomanr.composables.bottomsheet.BasicModalBottomSheet
import com.nomanr.composables.bottomsheet.SheetState
import com.nomanr.composables.bottomsheet.rememberModalBottomSheetState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun ModalBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    sheetGesturesEnabled: Boolean = true,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    content: @Composable ColumnScope.() -> Unit,
) {
    if (isVisible) {
        BasicModalBottomSheet(
            modifier = modifier,
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
            sheetGesturesEnabled = sheetGesturesEnabled,
            containerColor = AppTheme.colors.background,
            scrimColor = AppTheme.colors.scrim,
            shape = BottomSheetDefaults.ModalBottomSheetShape,
            dragHandle = dragHandle,
            content = content,
        )
    }
}

internal object BottomSheetDefaults {
    private val DragHandleHeight = 6.dp
    private val DragHandleWidth = 36.dp
    private val DragHandleShape = RoundedCornerShape(50)
    private val DragHandlePadding = 12.dp
    val ModalBottomSheetShape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
    )

    @Composable
    fun DragHandle() {
        Box(
            modifier =
                Modifier
                    .padding(DragHandlePadding)
                    .background(color = Color.Unspecified, shape = RoundedCornerShape(12.dp)),
        ) {
            Spacer(
                Modifier
                    .size(width = DragHandleWidth, height = DragHandleHeight)
                    .background(color = LocalContentColor.current, shape = DragHandleShape),
            )
        }
    }
}

@Composable
private fun ModalBottomSheetPreview() {
    ModalBottomSheet(isVisible = true, onDismissRequest = { }) {
        Column {
            Text("Hello bottom sheet")
            Spacer(modifier = Modifier.height(300.dp))
        }
    }
}

@Preview
@Composable
internal fun ModalBottomSheetLightPreview() {
    AppPreview { ModalBottomSheetPreview() }
}

@Preview
@Composable
internal fun ModalBottomSheetDarkPreview() {
    AppPreview(isDarkTheme = true) { ModalBottomSheetPreview() }
}
