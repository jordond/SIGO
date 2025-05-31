package app.sigot.core.ui.navigation.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import app.sigot.core.ui.components.BottomSheetDefaults
import app.sigot.core.ui.components.ModalBottomSheet
import app.sigot.core.ui.components.bottomsheet.ModalBottomSheetDefaults
import app.sigot.core.ui.components.bottomsheet.ModalBottomSheetProperties
import app.sigot.core.ui.contentColorFor

@Composable
public fun ModalBottomSheetLayout(
    bottomSheetNavigator: BottomSheetNavigator,
    modifier: Modifier = Modifier,
    sheetModifier: Modifier = Modifier,
    sheetMaxWidth: Dp = BottomSheetDefaults.parent.SheetMaxWidth,
    shape: Shape = BottomSheetDefaults.parent.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.parent.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    scrimColor: Color = BottomSheetDefaults.parent.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.parent.windowInsets },
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        bottomSheetNavigator.sheetInitializer()
        content()
    }

    ModalBottomSheet(
        onDismissRequest = bottomSheetNavigator.onDismissRequest,
        modifier = sheetModifier,
        sheetState = bottomSheetNavigator.sheetState,
        content = bottomSheetNavigator.sheetContent,
        isVisible = remember(bottomSheetNavigator.sheetEnabled) {
            bottomSheetNavigator.sheetEnabled
        },
        sheetGesturesEnabled = dragHandle != null,
        dragHandle = dragHandle,
    )
}
