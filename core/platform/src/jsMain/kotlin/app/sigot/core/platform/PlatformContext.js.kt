package app.sigot.core.platform

import androidx.compose.runtime.Composable
import coil3.PlatformContext

internal actual val context: PlatformContext = PlatformContext.INSTANCE

public actual val isAndroid: Boolean = false

@Composable
public actual fun context(): PlatformContext = PlatformContext.INSTANCE
