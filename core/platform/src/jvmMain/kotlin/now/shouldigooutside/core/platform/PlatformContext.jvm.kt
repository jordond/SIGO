package now.shouldigooutside.core.platform

import androidx.compose.runtime.Composable
import coil3.PlatformContext

public actual val context: PlatformContext
    get() = PlatformContext.INSTANCE

public actual val isAndroid: Boolean = false

public actual val platform: Platform = Platform.Desktop

@Composable
public actual fun context(): PlatformContext = context
