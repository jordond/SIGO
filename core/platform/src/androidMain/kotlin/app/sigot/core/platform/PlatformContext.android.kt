package app.sigot.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil3.PlatformContext

public actual val context: PlatformContext
    get() = TODO("Need to get it from DI")

public actual val isAndroid: Boolean = true

@Composable
public actual fun context(): PlatformContext = LocalContext.current
