package now.shouldigooutside.test

import now.shouldigooutside.core.domain.settings.IsSimulateFailureUseCase

public class FakeIsSimulateFailureUseCase(
    public var shouldFail: Boolean = false,
) : IsSimulateFailureUseCase {
    override fun invoke(): Boolean = shouldFail
}
