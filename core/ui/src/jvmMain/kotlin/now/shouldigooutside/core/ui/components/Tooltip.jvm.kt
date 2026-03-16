package now.shouldigooutside.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
internal actual fun windowContainerWidthInPx(): Int = LocalWindowInfo.current.containerSize.width
