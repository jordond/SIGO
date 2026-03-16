package now.shouldigooutside.core.foundation

import kotlinx.datetime.TimeZone

public fun interface TimezoneProvider {
    public fun provide(): TimeZone
}

internal class DefaultTimezoneProvider : TimezoneProvider {
    override fun provide(): TimeZone = TimeZone.currentSystemDefault()
}
