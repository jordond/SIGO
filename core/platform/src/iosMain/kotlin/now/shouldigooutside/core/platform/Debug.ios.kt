package now.shouldigooutside.core.platform

import kotlin.native.Platform

public actual val isDebug: Boolean = Platform.isDebugBinary
