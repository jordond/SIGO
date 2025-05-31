package app.sigot.core.domain.settings

import kotlinx.coroutines.flow.Flow

public interface HapticsUseCase {
    public fun isEnabled(): Boolean

    public fun updates(): Flow<Boolean>

    public fun update(value: Boolean)
}
