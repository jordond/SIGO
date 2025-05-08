package app.sigot.core.ui.icons.lucide

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import app.sigot.core.ui.icons.LocalPlatformIcon
import app.sigot.core.ui.icons.PlatformIcon

public object Lucide

public val Lucide.Share: ImageVector
    @Composable
    get() = if (LocalPlatformIcon.current == PlatformIcon.Android) ShareAndroid else ShareIos
