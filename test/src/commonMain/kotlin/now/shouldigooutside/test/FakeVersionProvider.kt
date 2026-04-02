package now.shouldigooutside.test

import now.shouldigooutside.core.domain.VersionProvider
import now.shouldigooutside.core.model.Version

public class FakeVersionProvider(
    public var version: Version = Version(code = 1, name = "1.0.0"),
) : VersionProvider {
    override fun provide(): Version = version
}
