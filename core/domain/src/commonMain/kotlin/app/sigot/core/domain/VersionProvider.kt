package app.sigot.core.domain

import app.sigot.core.model.Version

public interface VersionProvider {
    public fun provide(): Version
}
