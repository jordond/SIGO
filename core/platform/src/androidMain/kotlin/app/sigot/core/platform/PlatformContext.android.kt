package app.sigot.core.platform

import android.content.Context
import androidx.compose.runtime.Composable
import app.sigot.core.platform.di.getKoinInstance
import coil3.PlatformContext

public actual val context: PlatformContext
    get() = getKoinInstance<Context>()

public actual val isAndroid: Boolean = true

public actual val platform: Platform = Platform.Android

@Composable
public actual fun context(): PlatformContext = context
