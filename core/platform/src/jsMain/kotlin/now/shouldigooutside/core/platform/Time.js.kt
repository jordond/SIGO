package now.shouldigooutside.core.platform

import kotlin.js.Date

public actual fun currentTimeMillis(): Long = Date().getTime().toLong()
