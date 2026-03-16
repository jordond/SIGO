package now.shouldigooutside.core.platform

import android.content.Context
import androidx.compose.runtime.Composable
import coil3.PlatformContext
import now.shouldigooutside.core.platform.di.getKoinInstance

public actual val context: PlatformContext
    get() = getKoinInstance<Context>()

public actual val isAndroid: Boolean = true

public actual val platform: Platform = Platform.Android

@Composable
public actual fun context(): PlatformContext = context
