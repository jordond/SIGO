package now.shouldigooutside.core.domain

import now.shouldigooutside.core.model.Version

public interface VersionProvider {
    public fun provide(): Version
}
