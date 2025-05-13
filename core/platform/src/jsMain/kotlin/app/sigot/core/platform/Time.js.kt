package app.sigot.core.platform

import kotlin.js.Date

public actual fun currentTimeMillis(): Long = Date().getTime().toLong()
